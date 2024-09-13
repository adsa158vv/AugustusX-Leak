import re
import random
from pathlib import Path

# 指定源代码目录
source_directory = Path(r'E:\Projects\AugustusX\src\net\augustus\modules')

# 构造函数的正则表达式模板
constructor_patterns = {
    'BooleanValue': r'new\s+BooleanValue\s*\((\d+),',
    'StringValue': r'new\s+StringValue\s*\((\d+),',
    'BooleansSetting': r'new\s+BooleansSetting\s*\((\d+),',
    'ColorSetting': r'new\s+ColorSetting\s*\((\d+),',
    'DoubleValue': r'new\s+DoubleValue\s*\((\d+),'
}

# 遍历指定目录及其子目录下的所有 Java 文件
for java_file in source_directory.rglob('*.java'):
    # 初始化当前文件的 id 计数器
    id_counter = 1
    # 初始化一个集合，用于存储当前文件中已使用的 id
    used_ids = set()

    # 读取文件内容
    with open(java_file, 'r', encoding='utf-8') as f:
        original_content = f.read()

    # 对每个构造函数进行匹配和替换
    for constructor, pattern in constructor_patterns.items():
        # 编译正则表达式
        regex = re.compile(pattern)
        # 存储匹配的 id
        matches = regex.findall(original_content)
        for match in matches:
            # 确保 id 是唯一的
            while True:
                new_id = random.randint(1, 16384)
                if new_id not in used_ids and str(new_id) != match:
                    break
            # 替换匹配的 id
            original_content = original_content.replace(f'new {constructor}({match},', f'new {constructor}({new_id},', 1)
            # 将新 id 添加到已使用集合中
            used_ids.add(new_id)

    # 重新打开文件以写入更新后的内容
    with open(java_file, 'w', encoding='utf-8') as f:
        f.write(original_content)

    # 打印更新信息
    print(f'Updated {java_file}')

print('Finished updating all files.')
