// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.maliciousUsageTests;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.core.PathManager;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Immortius
 */
@RegisterSystem
public class MaliciousTestSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(MaliciousTestSystem.class);

    private int numTests;
    private int passedTests;

    @Override
    public void initialise() {
        numTests = 0;
        passedTests = 0;
        testFileWrite();
        testReflectionConstruction();
        logger.info("Malicious Usage Test Result: {}/{} malicious usage tests passed", passedTests, numTests);
    }

    @Override
    public void shutdown() {
    }

    public void testFileWrite() {
        numTests++;
        try {
            FileSystem fileSystem = FileSystems.getDefault();
            Path path = fileSystem.getPath("garbage.out");
            Files.write(path, Lists.newArrayList("Blah", "Balh"), Charset.defaultCharset());
        } catch (IOException e) {
            logger.info("IO exception in test file write", e);
            return;
        } catch (NoClassDefFoundError | SecurityException e) {
            passedTests++;
            return;
        }
        logger.warn("File.write completed without error.");
    }

    public void testReflectionConstruction() {
        numTests++;
        try {
            PathManager.getInstance();
        } catch (NoClassDefFoundError e) {
            passedTests++;
            return;
        }
        logger.warn("Succeeded in getting an instance.");
    }

}
