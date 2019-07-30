package casia.isiteam.preprocess;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Tokenizer {
    public static List<String> tokenize(String text, boolean mode) {
        // 如果mode是true，IKAnalyzer将启用智能切词优化
        Analyzer analyzer = new IKAnalyzer(mode);
        try {
            return splitTokens(analyzer, text);
        } catch (Exception e) {
            System.err.println("Exceptions happened when splitTokens is running");
            e.printStackTrace();
            analyzer.close();
            return null;
        }
    }

    public static ArrayList<String> splitTokens(Analyzer analyzer, String text) throws Exception {
        ArrayList<String> list = new ArrayList<String>();

        // 分词流，即将对象分词后所得的Token在内存中以流的方式存在，也说是说如果在取得Token必须从TokenStream中获取，而分词对象可以是文档文本，也可以是查询文本。
        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text));
        tokenStream.reset();
        // 这个有点特殊，它表示tokenStream中的当前token与前一个token在实际的原文本中相隔的词语数量，用于短语查询。比如：
        // 在tokenStream中[2:a]的前一个token是[1:I'm]，
        // 它们在原文本中相隔的词语数是1，则token="a"的PositionIncrementAttribute值为1；
        PositionIncrementAttribute positionIncrementAttribute =
                        tokenStream.addAttribute(PositionIncrementAttribute.class);

        // 问题说明：这里需要使用jdk1.7,如果使用jdk1.8或者jdk1.6则会出现报错信息
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        int position = 0;
        while (tokenStream.incrementToken()) {
            int increment = positionIncrementAttribute.getPositionIncrement();
            if (increment > 0)
                position = position + increment;

            String tmp = charTermAttribute.toString();
            // 将第一位是数字的token直接过滤掉。一般来说，这种词对于分类不是很重要
            if (!Character.isDigit(tmp.charAt(0))) {
                list.add(charTermAttribute.toString());
            }
            // 表示token的首字母和尾字母在原文本中的位置。比如I'm的位置信息就是(0,3)，需要注意的是startOffset与endOffset的差值并不一定就是termText.length()，
            // 因为可能term已经用stemmer或者其他过滤器处理过；
            // OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
            // 表示token词典类别信息，默认为“Word”，比如I'm就属于<APOSTROPHE>，有撇号的类型；
            // TypeAttribute typeAttribute = tokenStream.addAttribute(TypeAttribute.class);

            // int startOffset = offsetAttribute.startOffset();
            // int endOffset = offsetAttribute.endOffset();
            // String term = "输出结果为：" + charTermAttribute.toString();
            // System.out.println("第" + position + "个分词，分词内容是:[" + term + "]" + "，分词内容的开始结束位置为：("
            // + startOffset + "-->" + endOffset + ")，类型是：" + typeAttribute.type());
        }
        tokenStream.close();
        return list;
    }
}
