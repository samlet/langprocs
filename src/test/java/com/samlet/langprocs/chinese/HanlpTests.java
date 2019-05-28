package com.samlet.langprocs.chinese;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.suggest.Suggester;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.hankcs.hanlp.utility.LexiconUtility;
import org.junit.Test;

import java.util.List;

import static com.hankcs.hanlp.corpus.tag.Nature.n;

public class HanlpTests {
    @Test
    public void testDefaultSegment() {
        List<Term> termList = HanLP.segment("苹果电脑可以运行开源阿尔法狗代码吗");
        System.out.println(termList);
    }

    @Test
    public void testCustomNatureDict() {
        // 还可以直接插入到用户词典
        CustomDictionary.insert("阿尔法狗", "科技名词 1024");
        StandardTokenizer.SEGMENT.enablePartOfSpeechTagging(true);  // 依然支持隐马词性标注
        List<Term> termList = HanLP.segment("苹果电脑可以运行开源阿尔法狗代码吗");
        System.out.println(termList);
        // 1.6.5之后Nature不再是枚举类型，无法switch。但终于不再涉及反射了，在各种JRE环境下都更稳定。
        for (Term term : termList) {
            if (term.nature == n) {
                System.out.printf("** [%s] : %s\n", "名词", term.word);
            } else {
                System.out.println(String.format("%s - %s", term.nature.toString(), term.word));
            }
        }
    }

    @Test
    public void testCustomNatureSet(){
        Nature pcNature = Nature.fromString("科技名词");
        System.out.println(pcNature);
        // 可以动态添加一个
        pcNature = Nature.create("科技名词");
        System.out.println(pcNature);
        // 可以将它赋予到某个词语
        LexiconUtility.setAttribute("苹果电脑", pcNature);
        // LexiconUtility.setAttribute("阿尔法狗", "人工智能 2048 科技名词 1024");
        LexiconUtility.setAttribute("阿尔法狗", pcNature);
        List<Term> termList = HanLP.segment("苹果电脑可以运行开源阿尔法狗代码吗");
        System.out.println(termList);
        for (Term term : termList)
        {
            System.out.println(term);
            if (term.nature == pcNature)
                System.out.printf("找到了 [%s] : %s\n", pcNature, term.word);
        }
    }

    // https://github.com/hankcs/HanLP#14-%E5%85%B3%E9%94%AE%E8%AF%8D%E6%8F%90%E5%8F%96
    @Test
    public void testTextRank(){
        String content = "程序员(英文Programmer)是从事程序开发、维护的专业人员。一般将程序员分为程序设计人员和程序编码人员，但两者的界限并不非常清楚，特别是在中国。软件从业人员分为初级程序员、高级程序员、系统分析员和项目经理四大类。";
        List<String> keywordList = HanLP.extractKeyword(content, 5);
        System.out.println(keywordList);
    }

    @Test
    public void testSummary(){
        String document = "算法可大致分为基本算法、数据结构的算法、数论算法、计算几何的算法、图的算法、动态规划以及数值分析、加密算法、排序算法、检索算法、随机化算法、并行算法、厄米变形模型、随机森林算法。\n" +
                "算法可以宽泛的分为三类，\n" +
                "一，有限的确定性算法，这类算法在有限的一段时间内终止。他们可能要花很长时间来执行指定的任务，但仍将在一定的时间内终止。这类算法得出的结果常取决于输入值。\n" +
                "二，有限的非确定算法，这类算法在有限的时间内终止。然而，对于一个（或一些）给定的数值，算法的结果并不是唯一的或确定的。\n" +
                "三，无限的算法，是那些由于没有定义终止定义条件，或定义的条件无法由输入的数据满足而不终止运行的算法。通常，无限算法的产生是由于未能确定的定义终止条件。";
        List<String> sentenceList = HanLP.extractSummary(document, 3);
        System.out.println(sentenceList);
    }

    @Test
    public void testExtractPhrase(){
        String text = "算法工程师\n" +
                "算法（Algorithm）是一系列解决问题的清晰指令，也就是说，能够对一定规范的输入，在有限时间内获得所要求的输出。如果一个算法有缺陷，或不适合于某个问题，执行这个算法将不会解决这个问题。不同的算法可能用不同的时间、空间或效率来完成同样的任务。一个算法的优劣可以用空间复杂度与时间复杂度来衡量。算法工程师就是利用算法处理事物的人。\n" +
                "\n" +
                "1职位简介\n" +
                "算法工程师是一个非常高端的职位；\n" +
                "专业要求：计算机、电子、通信、数学等相关专业；\n" +
                "学历要求：本科及其以上的学历，大多数是硕士学历及其以上；\n" +
                "语言要求：英语要求是熟练，基本上能阅读国外专业书刊；\n" +
                "必须掌握计算机相关知识，熟练使用仿真工具MATLAB等，必须会一门编程语言。\n" +
                "\n" +
                "2研究方向\n" +
                "视频算法工程师、图像处理算法工程师、音频算法工程师 通信基带算法工程师\n" +
                "\n" +
                "3目前国内外状况\n" +
                "目前国内从事算法研究的工程师不少，但是高级算法工程师却很少，是一个非常紧缺的专业工程师。算法工程师根据研究领域来分主要有音频/视频算法处理、图像技术方面的二维信息算法处理和通信物理层、雷达信号处理、生物医学信号处理等领域的一维信息算法处理。\n" +
                "在计算机音视频和图形图像技术等二维信息算法处理方面目前比较先进的视频处理算法：机器视觉成为此类算法研究的核心；另外还有2D转3D算法(2D-to-3D conversion)，去隔行算法(de-interlacing)，运动估计运动补偿算法(Motion estimation/Motion Compensation)，去噪算法(Noise Reduction)，缩放算法(scaling)，锐化处理算法(Sharpness)，超分辨率算法(Super Resolution),手势识别(gesture recognition),人脸识别(face recognition)。\n" +
                "在通信物理层等一维信息领域目前常用的算法：无线领域的RRM、RTT，传送领域的调制解调、信道均衡、信号检测、网络优化、信号分解等。\n" +
                "另外数据挖掘、互联网搜索算法也成为当今的热门方向。\n" +
                "算法工程师逐渐往人工智能方向发展。";
        List<String> phraseList = HanLP.extractPhrase(text, 10);
        System.out.println(phraseList);
    }

    @Test
    public void testSuggester(){
        Suggester suggester = new Suggester();
        String[] titleArray =
                (
                        "威廉王子发表演说 呼吁保护野生动物\n" +
                                "《时代》年度人物最终入围名单出炉 普京马云入选\n" +
                                "“黑格比”横扫菲：菲吸取“海燕”经验及早疏散\n" +
                                "日本保密法将正式生效 日媒指其损害国民知情权\n" +
                                "英报告说空气污染带来“公共健康危机”"
                ).split("\\n");
        for (String title : titleArray)
        {
            suggester.addSentence(title);
        }

        System.out.println(suggester.suggest("发言", 1));       // 语义
        System.out.println(suggester.suggest("危机公共", 1));   // 字符
        System.out.println(suggester.suggest("mayun", 1));      // 拼音

    }
}
