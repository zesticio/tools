package com.zestic.setting;

import com.zestic.core.io.FileUtil;
import com.zestic.core.io.IoUtil;
import com.zestic.core.io.resource.Resource;
import com.zestic.core.lang.Assert;
import com.zestic.core.util.CharUtil;
import com.zestic.core.util.CharsetUtil;
import com.zestic.core.util.ReUtil;
import com.zestic.core.util.StrUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class SettingLoader {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SettingLoader.class);

    private final static char COMMENT_FLAG_PRE = '#';
    private char assignFlag = '=';
    private String varRegex = "\\$\\{(.*?)\\}";
    private final Charset charset;
    private final boolean isUseVariable;
    private final GroupedMap groupedMap;

    public SettingLoader(GroupedMap groupedMap) {
        this(groupedMap, CharsetUtil.CHARSET_UTF_8, false);
    }

    public SettingLoader(GroupedMap groupedMap, Charset charset, boolean isUseVariable) {
        this.groupedMap = groupedMap;
        this.charset = charset;
        this.isUseVariable = isUseVariable;
    }

    public boolean load(Resource resource) {
        if (resource == null) {
            throw new NullPointerException("Null setting url define!");
        }
        logger.debug("Load setting file [{" + resource + "}]");
        InputStream settingStream = null;
        try {
            settingStream = resource.getStream();
            load(settingStream);
        } catch (Exception e) {
            logger.error("", e);
            return false;
        } finally {
            IoUtil.close(settingStream);
        }
        return true;
    }

    synchronized public boolean load(InputStream settingStream) throws IOException {
        this.groupedMap.clear();
        BufferedReader reader = null;
        try {
            reader = IoUtil.getReader(settingStream, this.charset);
            // 分组
            String group = null;

            String line;
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                // 跳过注释行和空行
                if (StrUtil.isBlank(line) || StrUtil.startWith(line, COMMENT_FLAG_PRE)) {
                    continue;
                }

                // 记录分组名
                if (StrUtil.isSurround(line, CharUtil.BRACKET_START, CharUtil.BRACKET_END)) {
                    group = line.substring(1, line.length() - 1).trim();
                    continue;
                }

                final String[] keyValue = StrUtil.splitToArray(line, this.assignFlag, 2);
                // 跳过不符合键值规范的行
                if (keyValue.length < 2) {
                    continue;
                }

                String value = keyValue[1].trim();
                // 替换值中的所有变量变量（变量必须是此行之前定义的变量，否则无法找到）
                if (this.isUseVariable) {
                    value = replaceVar(group, value);
                }
                this.groupedMap.put(group, keyValue[0].trim(), value);
            }
        } finally {
            IoUtil.close(reader);
        }
        return true;
    }

    public void setVarRegex(String regex) {
        this.varRegex = regex;
    }

    public void setAssignFlag(char assignFlag) {
        this.assignFlag = assignFlag;
    }

    public void store(String absolutePath) {
        store(FileUtil.touch(absolutePath));
    }

    public void store(File file) {
        Assert.notNull(file, "File to store must be not null !");
        logger.debug("Store Setting to [{}]..." + file.getAbsolutePath());
        PrintWriter writer = null;
        try {
            writer = FileUtil.getPrintWriter(file, charset, false);
            store(writer);
        } finally {
            IoUtil.close(writer);
        }
    }

    synchronized private void store(PrintWriter writer) {
        for (Entry<String, LinkedHashMap<String, String>> groupEntry : this.groupedMap.entrySet()) {
            writer.println(StrUtil.format("{}{}{}", CharUtil.BRACKET_START, groupEntry.getKey(), CharUtil.BRACKET_END));
            for (Entry<String, String> entry : groupEntry.getValue().entrySet()) {
                writer.println(StrUtil.format("{} {} {}", entry.getKey(), this.assignFlag, entry.getValue()));
            }
        }
    }

    private String replaceVar(String group, String value) {
        final Set<String> vars = ReUtil.findAll(varRegex, value, 0, new HashSet<>());
        String key;
        for (String var : vars) {
            key = ReUtil.get(varRegex, var, 1);
            if (StrUtil.isNotBlank(key)) {
                String varValue = this.groupedMap.get(group, key);
                if (null == varValue) {
                    final List<String> groupAndKey = StrUtil.split(key, CharUtil.DOT, 2);
                    if (groupAndKey.size() > 1) {
                        varValue = this.groupedMap.get(groupAndKey.get(0), groupAndKey.get(1));
                    }
                }
                if (null == varValue) {
                    varValue = System.getProperty(key);
                }
                if (null == varValue) {
                    varValue = System.getenv(key);
                }

                if (null != varValue) {
                    value = value.replace(var, varValue);
                }
            }
        }
        return value;
    }
}
