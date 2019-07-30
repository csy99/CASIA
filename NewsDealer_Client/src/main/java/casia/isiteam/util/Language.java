package casia.isiteam.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 由于新闻由多种语言构成，做出甄别
 * 
 * @author CSY
 *
 */
public class Language {
    private final static String regEx = "([\\u4E00-\\u9FA5|\\p{P}|0-9a-zA-Z|\\s|^|%|`|~|+|-"
                    + "|\\！|\\,|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]*)";

    public static boolean isChineseOrEnglish(String str) {
        Pattern p = Pattern.compile(regEx);
        // 为了保证运行速度，只读取文档前10个字符，默认后面不会出现其他文字的字符
        String sub = str.substring(0, Math.min(10, str.length()));
        Matcher m = p.matcher(sub);
        return m.matches();
    }
}
