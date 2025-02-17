package com.mojang.authlib.yggdrasil.request;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

public class RefreshRequest {
   private final String clientToken;
   private final String accessToken;
   private final GameProfile selectedProfile;
   private final boolean requestUser = true;

   public RefreshRequest(YggdrasilUserAuthentication authenticationService) {
      this(authenticationService, null);
   }

   public RefreshRequest(YggdrasilUserAuthentication authenticationService, GameProfile profile) {
      this.clientToken = authenticationService.getAuthenticationService().getClientToken();
      this.accessToken = authenticationService.getAuthenticatedToken();
      this.selectedProfile = profile;
   }
}
