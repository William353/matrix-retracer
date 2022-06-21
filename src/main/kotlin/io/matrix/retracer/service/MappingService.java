package io.matrix.retracer.service;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.matrix.retracer.Constants.MAPPING_TXT;

/**
 * 混淆(mapping文件)相关
 */
@Service
public class MappingService {

    private String absoluteDir;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public MappingService(@Value("${retracer.dataDir}") @NotNull String dataDir) {
        this.absoluteDir = System.getProperty("user.dir") + dataDir;
    }

    public void saveMapping(String appId, String appVersionName, Long appVersionCode, MultipartFile multipartFile) throws IOException {
        File dir = new File(absoluteDir, fileNameFromAppInfo(appId, appVersionName, appVersionCode));
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                logger.error("Failed to mkdirs " + dir);
            }
        }

        File mapping = new File(dir, MAPPING_TXT);
        if (mapping.exists()) {
            logger.warn(mapping + " already exists");
            mapping.delete();
        }

        try {
            mapping.createNewFile();
        } catch (IOException e) {
            logger.error("Create mapping file " + mapping.getCanonicalPath() + " failed", e);
            throw e;
        }

        logger.info("Saving mapping file: " + mapping.getCanonicalPath());

        try {
            multipartFile.transferTo(mapping);
        } catch (Throwable e) {
            logger.error("Save mapping file `${mapping.canonicalPath}` failed", e);
            throw e;
        }
    }

    /**
     * 把methodMapping.txt解析到Map中，key为methodId，value为trace明文
     */
    public Map<Integer, String> getMapping(String appId, String appVersionName, Long appVersionCode) {
        return readMappingFile(getMappingFile(appId, appVersionName, appVersionCode));
    }

    private File getMappingFile(String appId, String appVersionName, Long appVersionCode) {
        File dir = new File(absoluteDir, fileNameFromAppInfo(appId, appVersionName, appVersionCode));
        return new File(dir, MAPPING_TXT);
    }

    private String fileNameFromAppInfo(String appId, String appVersionName, Long appVersionCode) {
        return appId + File.separator + appVersionName + File.separator + appVersionCode;
    }

    /**
     * 读mapdding到内存
     */
    private Map<Integer, String> readMappingFile(File file) {
        Map<Integer, String> methodMap = new HashMap<>();
        BufferedReader reader = null;
        String tempString = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((tempString = reader.readLine()) != null) {
                String[] contents = tempString.split(",");
                methodMap.put(Integer.parseInt(contents[0]), contents[2].replace('\n', ' '));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return methodMap;
    }
}
