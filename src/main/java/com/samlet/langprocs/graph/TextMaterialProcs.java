package com.samlet.langprocs.graph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class TextMaterialProcs {
    public void procParallel(String eng, String cmn){
        System.out.println(String.format("%s - %s", eng, cmn));
    }

    public static void main(String[] args) throws IOException {
        String fileName="/pi/ai/seq2seq/cmn-eng/cmn.txt";
        TextMaterialProcs procs=new TextMaterialProcs();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(line ->{
                String[] parts=line.split("\t");
                procs.procParallel(parts[0], parts[1]);
            });
        }
    }
}
