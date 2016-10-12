package cn.enilu.flash.core.util;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.Map;

public final class VelocityUtil {

	private VelocityUtil() {
	}

	public static String renderTemplate(VelocityEngine engine,
			String templateLocation, Map<String, Object> model) {
		return VelocityEngineUtils.mergeTemplateIntoString(engine,
				templateLocation, "UTF-8", model);
	}

}
