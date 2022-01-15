package api.training.utils;

import api.training.exceptions.Exceptions;
import io.qameta.allure.Allure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AllureUtils {

	public static void addAllureAttachment(String name, File file) {
		Path content = Paths.get(file.getAbsolutePath());
		try {
			Allure.addAttachment(name, Files.newInputStream(content));
		} catch (IOException e) {
			throw new Exceptions.AllureAddAttachmentException(e);
		}
	}
}