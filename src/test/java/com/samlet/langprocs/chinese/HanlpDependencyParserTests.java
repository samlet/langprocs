package com.samlet.langprocs.chinese;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.dependency.IDependencyParser;
import com.hankcs.hanlp.dependency.nnparser.NeuralNetworkDependencyParser;
import org.junit.Test;

import java.util.Map;

public class HanlpDependencyParserTests {
    @Test
    public void testDeps() {
        CoNLLSentence sentence = HanLP.parseDependency("徐先生还具体帮助他确定了把画雄鹰、松鼠和麻雀作为主攻目标。");
        // 也可以用基于ArcEager转移系统的依存句法分析器
        // IDependencyParser parser = new KBeamArcEagerDependencyParser();
        // CoNLLSentence sentence = parser.parse("徐先生还具体帮助他确定了把画雄鹰、松鼠和麻雀作为主攻目标。");
        System.out.println(sentence);
        // 可以方便地遍历它
        for (CoNLLWord word : sentence) {
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
        }
        // 也可以直接拿到数组，任意顺序或逆序遍历
        CoNLLWord[] wordArray = sentence.getWordArray();
        for (int i = wordArray.length - 1; i >= 0; i--) {
            CoNLLWord word = wordArray[i];
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
        }
        // 还可以直接遍历子树，从某棵子树的某个节点一路遍历到虚根
        CoNLLWord head = wordArray[12];
        while ((head = head.HEAD) != null) {
            if (head == CoNLLWord.ROOT) System.out.println(head.LEMMA);
            else System.out.printf("%s --(%s)--> ", head.LEMMA, head.DEPREL);
        }
    }

    @Test
    public void testDepLabels(){
        IDependencyParser parser = new NeuralNetworkDependencyParser().enableDeprelTranslator(false);
        System.out.println(parser.parse("徐先生还具体帮助他确定了把画雄鹰、松鼠和麻雀作为主攻目标。"));
        Map<String, String> trans=parser.getDeprelTranslator();
        System.out.println(trans.keySet());
        System.out.println(trans.values());
    }
    @Test
    public void testDepLabelsZh(){
        IDependencyParser parser = new NeuralNetworkDependencyParser().enableDeprelTranslator(true);
        System.out.println(parser.parse("徐先生还具体帮助他确定了把画雄鹰、松鼠和麻雀作为主攻目标。"));
    }
}
