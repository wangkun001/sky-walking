package com.ai.cloud.skywalking.plugin.interceptor.enhance;

import com.ai.cloud.skywalking.plugin.interceptor.enhance.exception.FailedLoadEnhanceCodeSegmentException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CodeGenerator {
    private static final String              ENHANCE_CODE_RESOURCE_BASE_DIR = "/enhance-code/";
    private static       Map<String, String> fileNameToEnhanceSegmentCode   = new ConcurrentHashMap<String, String>();

    private static String loadCodeSegment(String fileName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(CodeGenerator.class.getResourceAsStream(ENHANCE_CODE_RESOURCE_BASE_DIR + fileName)));
        StringBuilder code = new StringBuilder();
        String codeSegment;
        while ((codeSegment = bufferedReader.readLine()) != null) {
            code.append(codeSegment);
        }

        return code.toString();
    }

    public static String generate(String fileName, Map<String, String> parameters) throws FailedLoadEnhanceCodeSegmentException {
        String segmentCode = fileNameToEnhanceSegmentCode.get(fileName);
        if (segmentCode == null) {
            try {
                segmentCode = loadCodeSegment(fileName);
            }catch (IOException e){
                throw new FailedLoadEnhanceCodeSegmentException("Failed to load file[" + fileName + "] segment code.", e);
            }

            fileNameToEnhanceSegmentCode.put(fileName, segmentCode);
        }

        for (Map.Entry<String, String> parameterEntry : parameters.entrySet()){
            segmentCode = segmentCode.replaceAll("%"+ parameterEntry.getKey() + "%", parameterEntry.getValue());
        }

        return segmentCode;
    }

    public static String generate(String fileName) throws FailedLoadEnhanceCodeSegmentException {
        return generate(fileName, new HashMap<String, String>());
    }

}
