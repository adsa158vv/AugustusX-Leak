package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.crypto.SecretKey;
import net.augustus.Augustus;
import net.augustus.events.EventReadPacket;
import net.augustus.modules.exploit.PostDis;
import net.augustus.utils.EventHandler;
import net.augustus.utils.Logger;
import net.augustus.utils.OioProxyChannelFactory;
import net.lenni0451.eventapi.manager.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import viamcp.ViaMCP;
import viamcp.handler.MCPDecodeHandler;
import viamcp.handler.MCPEncodeHandler;
import viamcp.utils.NettyUtil;

import static net.augustus.utils.interfaces.MM.mm;

public class NetworkManager extends SimpleChannelInboundHandler<Packet> {
   private static final Logger logger = new Logger();
   public static final AttributeKey<EnumConnectionState> attrKeyConnectionState = AttributeKey.valueOf("protocol");
   public static final LazyLoadBase<NioEventLoopGroup> CLIENT_NIO_EVENTLOOP = new LazyLoadBase<NioEventLoopGroup>() {
      protected NioEventLoopGroup load() {
         return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
      }
   };
   public static final LazyLoadBase<EpollEventLoopGroup> field_181125_e = new LazyLoadBase<EpollEventLoopGroup>() {
      protected EpollEventLoopGroup load() {
         return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
      }
   };
   public static final LazyLoadBase<LocalEventLoopGroup> CLIENT_LOCAL_EVENTLOOP = new LazyLoadBase<LocalEventLoopGroup>() {
      protected LocalEventLoopGroup load() {
         return new LocalEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
      }
   };
   private static Minecraft mc;
   private final EnumPacketDirection direction;
   private final Queue<NetworkManager.InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
   private final ReentrantReadWriteLock field_181680_j = new ReentrantReadWriteLock();

   private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
   private Channel channel;
   private SocketAddress socketAddress;
   private INetHandler packetListener;
   private IChatComponent terminationReason;
   private boolean isEncrypted;
   private boolean disconnected;

   public NetworkManager(EnumPacketDirection packetDirection) {
      this.direction = packetDirection;
   }

   @Override
   public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
      super.channelActive(p_channelActive_1_);
      this.channel = p_channelActive_1_.channel();
      this.socketAddress = this.channel.remoteAddress();

      try {
         this.setConnectionState(EnumConnectionState.HANDSHAKING);
      } catch (Throwable var3) {
         logger.fatal(var3);
      }
   }

   public void setConnectionState(EnumConnectionState newState) {
      this.channel.attr(attrKeyConnectionState).set(newState);
      this.channel.config().setAutoRead(true);
      //logger.debug("Enabled auto read");
   }

   @Override
   public void channelInactive(ChannelHandlerContext p_channelInactive_1_) throws Exception {
      this.closeChannel(new ChatComponentTranslation("disconnect.endOfStream"));
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) throws Exception {
      ChatComponentTranslation chatcomponenttranslation;
      if (p_exceptionCaught_2_ instanceof TimeoutException) {
         chatcomponenttranslation = new ChatComponentTranslation("disconnect.timeout");
      } else {
         chatcomponenttranslation = new ChatComponentTranslation("disconnect.genericReason", "Internal Exception: " + p_exceptionCaught_2_);
      }

      this.closeChannel(chatcomponenttranslation);
   }

   protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, Packet packet) throws Exception {
      if (this.channel.isOpen()) {
         if (mm.postDis.isToggled() && PostDis.getGrimPost() && PostDis.grimPostDelay(packet)) {
            mm.postDis.netWorkManagerHook(packet, this.packetListener, this.direction);
         } else {
            try {
               EventReadPacket eventReadPacket = new EventReadPacket(packet, this.packetListener, this.direction);
               EventHandler.call(eventReadPacket);
               if (eventReadPacket.isCancelled()) {
                  return;
               }

               packet.processPacket(this.packetListener);
            } catch (ThreadQuickExitException ignored) {
            }

         }
      }
   }



   public void setNetHandler(INetHandler handler) {
      Validate.notNull(handler, "packetListener");
      logger.debug("Set listener of {} to {}", this, handler);
      this.packetListener = handler;
   }

   public void sendPacket(Packet packet) {
      if ((
              packet instanceof C03PacketPlayer.C04PacketPlayerPosition
                      || packet instanceof C03PacketPlayer.C05PacketPlayerLook
                      || packet instanceof C03PacketPlayer.C06PacketPlayerPosLook
      )
              && Minecraft.getMinecraft().theWorld != null
              && Minecraft.getMinecraft().thePlayer != null) {
         C03PacketPlayer c03PacketPlayer = (C03PacketPlayer)packet;
         Minecraft.getMinecraft().thePlayer.rotIncrement = 3;
         if (!(packet instanceof C03PacketPlayer.C05PacketPlayerLook)) {
            Minecraft.getMinecraft().thePlayer.setLastServerPosition(Minecraft.getMinecraft().thePlayer.getSeverPosition());
            Minecraft.getMinecraft()
                    .thePlayer
                    .setSeverPosition(new Vec3(c03PacketPlayer.getPositionX(), c03PacketPlayer.getPositionY(), c03PacketPlayer.getPositionZ()));
         }
      }

      if (this.isChannelOpen()) {
         this.flushOutboundQueue();
         this.dispatchPacket(packet, null);
      } else {
         this.field_181680_j.writeLock().lock();

         try {
            this.outboundPacketsQueue
                    .add(new NetworkManager.InboundHandlerTuplePacketListener(packet, (GenericFutureListener<? extends Future<? super Void>>[])null));
         } finally {
            this.field_181680_j.writeLock().unlock();
         }
      }
   }

   public void sendPacketDirect(Packet packetIn) {
      if ((packetIn instanceof C03PacketPlayer.C04PacketPlayerPosition || packetIn instanceof C03PacketPlayer.C06PacketPlayerPosLook)
              && Minecraft.getMinecraft().theWorld != null
              && Minecraft.getMinecraft().thePlayer != null) {
         C03PacketPlayer c03PacketPlayer = (C03PacketPlayer)packetIn;
         Minecraft.getMinecraft().thePlayer.setLastServerPosition(Minecraft.getMinecraft().thePlayer.getSeverPosition());
         Minecraft.getMinecraft()
                 .thePlayer
                 .setSeverPosition(new Vec3(c03PacketPlayer.getPositionX(), c03PacketPlayer.getPositionY(), c03PacketPlayer.getPositionZ()));
      }

      if (this.isChannelOpen()) {
         this.flushOutboundQueue();
         this.dispatchPacket(packetIn, null);
      } else {
         this.field_181680_j.writeLock().lock();

         try {
            this.outboundPacketsQueue
                    .add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener<? extends Future<? super Void>>[])null));
         } finally {
            this.field_181680_j.writeLock().unlock();
         }
      }
   }

   public void sendPacket(
           Packet packetIn, GenericFutureListener<? extends Future<? super Void>> listener, GenericFutureListener<? extends Future<? super Void>>... listeners
   ) {
      if (this.isChannelOpen()) {
         this.flushOutboundQueue();
         this.dispatchPacket(packetIn, ArrayUtils.add(listeners, 0, listener));
      } else {
         this.field_181680_j.writeLock().lock();

         try {
            this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, ArrayUtils.add(listeners, 0, listener)));
         } finally {
            this.field_181680_j.writeLock().unlock();
         }
      }
   }

   private void dispatchPacket(final Packet inPacket, final GenericFutureListener<? extends Future<? super Void>>[] futureListeners) {
      final EnumConnectionState enumconnectionstate = EnumConnectionState.getFromPacket(inPacket);
      final EnumConnectionState enumconnectionstate1 = this.channel.attr(attrKeyConnectionState).get();
      if (enumconnectionstate1 != enumconnectionstate) {
         //logger.debug("Disabled auto read");
         this.channel.config().setAutoRead(false);
      }

      if (this.channel.eventLoop().inEventLoop()) {
         if (enumconnectionstate != enumconnectionstate1) {
            this.setConnectionState(enumconnectionstate);
         }

         ChannelFuture channelfuture = this.channel.writeAndFlush(inPacket);
         if (futureListeners != null) {
            channelfuture.addListeners(futureListeners);
         }

         channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      } else {
         this.channel.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
               if (enumconnectionstate != enumconnectionstate1) {
                  NetworkManager.this.setConnectionState(enumconnectionstate);
               }

               ChannelFuture channelfuture1 = NetworkManager.this.channel.writeAndFlush(inPacket);
               if (futureListeners != null) {
                  channelfuture1.addListeners(futureListeners);
               }

               channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
         });
      }
   }

   private void flushOutboundQueue() {
      if (this.channel != null && this.channel.isOpen()) {
         this.field_181680_j.readLock().lock();

         try {
            while(!this.outboundPacketsQueue.isEmpty()) {
               NetworkManager.InboundHandlerTuplePacketListener networkmanager$inboundhandlertuplepacketlistener = this.outboundPacketsQueue.poll();
               this.dispatchPacket(networkmanager$inboundhandlertuplepacketlistener.packet, networkmanager$inboundhandlertuplepacketlistener.futureListeners);
            }
         } finally {
            this.field_181680_j.readLock().unlock();
         }
      }
   }

   public void processReceivedPackets() {
      this.flushOutboundQueue();
      if (this.packetListener instanceof ITickable) {
         ((ITickable)this.packetListener).update();
      }

      this.channel.flush();
   }

   public SocketAddress getRemoteAddress() {
      return this.socketAddress;
   }

   public void closeChannel(IChatComponent message) {
      if (this.channel.isOpen()) {
         this.channel.close().awaitUninterruptibly();
         this.terminationReason = message;
      }
   }

   public boolean isLocalChannel() {
      return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
   }

   public static NetworkManager func_181124_a(InetAddress p_181124_0_, int p_181124_1_, boolean p_181124_2_) {
      final NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
      Class<? extends SocketChannel> oclass;
      LazyLoadBase<? extends EventLoopGroup> lazyloadbase;
      if (Epoll.isAvailable() && p_181124_2_) {
         oclass = EpollSocketChannel.class;
         lazyloadbase = field_181125_e;
      } else {
         oclass = NioSocketChannel.class;
         lazyloadbase = CLIENT_NIO_EVENTLOOP;
      }

      Bootstrap bootstrap = new Bootstrap();
      if (Augustus.getInstance().getProxy() != null) {
         bootstrap.group(new OioEventLoopGroup());
         OioProxyChannelFactory oioProxyChannelFactory = new OioProxyChannelFactory(Augustus.getInstance().getProxy());
         bootstrap.channelFactory(oioProxyChannelFactory);
      } else {
         bootstrap.group(lazyloadbase.getValue());
         bootstrap.channel(oclass);
      }

      bootstrap.handler(
              new ChannelInitializer<Channel>() {
                 @Override
                 protected void initChannel(Channel p_initChannel_1_) throws Exception {
                    try {
                       p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
                    } catch (ChannelException var3) {
                    }

                    p_initChannel_1_.pipeline()
                            .addLast("timeout", new ReadTimeoutHandler(30))
                            .addLast("splitter", new MessageDeserializer2())
                            .addLast("decoder", new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))
                            .addLast("prepender", new MessageSerializer2())
                            .addLast("encoder", new MessageSerializer(EnumPacketDirection.SERVERBOUND))
                            .addLast("packet_handler", networkmanager);
                    if (p_initChannel_1_ instanceof SocketChannel && ViaMCP.getInstance().getVersion() > 47) {
                       UserConnection user = new UserConnectionImpl(p_initChannel_1_, true);
                       new ProtocolPipelineImpl(user);
                       p_initChannel_1_.pipeline()
                               .addBefore("encoder", "via-encoder", new MCPEncodeHandler(user))
                               .addBefore("decoder", "via-decoder", new MCPDecodeHandler(user));
                    }
                 }
              }
      );
      bootstrap.connect(p_181124_0_, p_181124_1_).syncUninterruptibly();
      return networkmanager;
   }

   public static NetworkManager provideLocalClient(SocketAddress address) {
      final NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
      new Bootstrap().group(CLIENT_LOCAL_EVENTLOOP.getValue()).handler(new ChannelInitializer<Channel>() {
         @Override
         protected void initChannel(Channel p_initChannel_1_) throws Exception {
            p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
         }
      }).channel(LocalChannel.class).connect(address).syncUninterruptibly();
      return networkmanager;
   }

   public void enableEncryption(SecretKey key) {
      this.isEncrypted = true;
      this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.createNetCipherInstance(2, key)));
      this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(1, key)));
   }

   public boolean getIsencrypted() {
      return this.isEncrypted;
   }

   public boolean isChannelOpen() {
      return this.channel != null && this.channel.isOpen();
   }

   public boolean hasNoChannel() {
      return this.channel == null;
   }

   public INetHandler getNetHandler() {
      return this.packetListener;
   }
   //
   private void dispatchUnregisteredPacket(final Packet inPacket) {
      final EnumConnectionState enumconnectionstate = EnumConnectionState.getFromPacket(inPacket);
      final EnumConnectionState enumconnectionstate2 = this.channel.attr(NetworkManager.attrKeyConnectionState).get();
      if (enumconnectionstate2 != enumconnectionstate) {
         NetworkManager.logger.debug("Disabled auto read");
         this.channel.config().setAutoRead(false);
      }
      if (this.channel.eventLoop().inEventLoop()) {
         if (enumconnectionstate != enumconnectionstate2) {
            this.setConnectionState(enumconnectionstate);
         }
         final ChannelFuture channelfuture = this.channel.writeAndFlush(inPacket);
         channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      }
      else {
         this.channel.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
               if (enumconnectionstate != enumconnectionstate2) {
                  NetworkManager.this.setConnectionState(enumconnectionstate);
               }
               final ChannelFuture channelfuture1 = NetworkManager.this.channel.writeAndFlush(inPacket);
               channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
         });
      }
   }
   public void sendUnregisteredPacket(final Packet packetIn) {
      if (this.isChannelOpen()) {
         this.flushOutboundQueue();
         this.dispatchUnregisteredPacket(packetIn);
      }
      else {
         this.readWriteLock.writeLock().lock();
         try {
            this.outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener<? extends Future<? super Void>>[])null));
         }
         finally {
            this.readWriteLock.writeLock().unlock();
         }
      }
   }
   public void receiveUnregisteredPacket(final Packet packet) {
      if (this.channel.isOpen()) {
         try {
            packet.processPacket(this.packetListener);
         }
         catch (ThreadQuickExitException ignored) {}
      }
   }
   //From Xylitol.

   public IChatComponent getExitMessage() {
      return this.terminationReason;
   }

   public void disableAutoRead() {
      this.channel.config().setAutoRead(false);
   }

   public void setCompressionTreshold(int treshold) {
      if (treshold >= 0) {
         if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
            ((NettyCompressionDecoder)this.channel.pipeline().get("decompress")).setCompressionTreshold(treshold);
         } else {
            NettyUtil.decodeEncodePlacement(this.channel.pipeline(), "decoder", "decompress", new NettyCompressionDecoder(treshold));
         }

         if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
            ((NettyCompressionEncoder)this.channel.pipeline().get("decompress")).setCompressionTreshold(treshold);
         } else {
            NettyUtil.decodeEncodePlacement(this.channel.pipeline(), "encoder", "compress", new NettyCompressionEncoder(treshold));
         }
      } else {
         if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
            this.channel.pipeline().remove("decompress");
         }

         if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
            this.channel.pipeline().remove("compress");
         }
      }
   }

   public void checkDisconnected() {
      if (this.channel != null && !this.channel.isOpen()) {
         if (!this.disconnected) {
            this.disconnected = true;
            if (this.getExitMessage() != null) {
               this.getNetHandler().onDisconnect(this.getExitMessage());
            } else if (this.getNetHandler() != null) {
               this.getNetHandler().onDisconnect(new ChatComponentText("Disconnected"));
            }
         } else {
            logger.warn("handleDisconnection() called twice");
         }
      }
   }



   static class InboundHandlerTuplePacketListener {
      private final Packet packet;
      private final GenericFutureListener<? extends Future<? super Void>>[] futureListeners;

      public InboundHandlerTuplePacketListener(Packet inPacket, GenericFutureListener<? extends Future<? super Void>>... inFutureListeners) {
         this.packet = inPacket;
         this.futureListeners = inFutureListeners;
      }
   }
}