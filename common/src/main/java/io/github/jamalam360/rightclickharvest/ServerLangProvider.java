package io.github.jamalam360.rightclickharvest;

import java.util.HashMap;
import java.util.Map;

public class ServerLangProvider {
	private static final Map<String, String> USE_HOE_MESSAGE_BY_LANGUAGE = new HashMap<>();
	private static final Map<String, String> REQUIRE_HOE_CONFIG_BY_LANGUAGE = new HashMap<>();

	public static String getUseHoeMessageByLanguage(String lang) {
		return USE_HOE_MESSAGE_BY_LANGUAGE.getOrDefault(lang, USE_HOE_MESSAGE_BY_LANGUAGE.get("en_us"));
	}

	public static String getRequireHoeConfigByLanguage(String lang) {
		return REQUIRE_HOE_CONFIG_BY_LANGUAGE.getOrDefault(lang, REQUIRE_HOE_CONFIG_BY_LANGUAGE.get("en_us"));
	}

	static {
		// gradle-auto-populated
		USE_HOE_MESSAGE_BY_LANGUAGE.put("de_de", "RightClickHarvest ist so konfiguriert, dass eine Hacke erforderlich ist - halte eine Hacke oder setze %s auf %s in der Konfiguration, um diese Anforderung zu deaktivieren. Diese Meldung wird nur einmal angezeigt.");
		REQUIRE_HOE_CONFIG_BY_LANGUAGE.put("de_de", "Benötigt Hacke");
		REQUIRE_HOE_CONFIG_BY_LANGUAGE.put("uk_ua", "Необхідна мотика");
		REQUIRE_HOE_CONFIG_BY_LANGUAGE.put("ko_kr", "수확에 괭이 필요");
		REQUIRE_HOE_CONFIG_BY_LANGUAGE.put("ja_jp", "クワを必須にする");
		REQUIRE_HOE_CONFIG_BY_LANGUAGE.put("pt_br", "Requer enxada");
		REQUIRE_HOE_CONFIG_BY_LANGUAGE.put("fr_fr", "Exiger une houe");
		USE_HOE_MESSAGE_BY_LANGUAGE.put("ru_ru", "Сбор урожая по ПКМ настроен на использование мотыги. Держите мотыгу или установите %s на %s в настройках, чтобы отключить это требование. Это сообщение будет показано только один раз.");
		REQUIRE_HOE_CONFIG_BY_LANGUAGE.put("ru_ru", "Требования мотыги");
		USE_HOE_MESSAGE_BY_LANGUAGE.put("en_us", "RightClickHarvest is configured to require holding a hoe for harvesting - do so, or set %s to %s in the config to disable this requirement. This message will only be shown once.");
		REQUIRE_HOE_CONFIG_BY_LANGUAGE.put("en_us", "Require Hoe");
		USE_HOE_MESSAGE_BY_LANGUAGE.put("zh_cn", "RightClickHarvest配置为需要锄头 - 请持有锄头或在配置中将%s设置为%s以禁用此要求。此消息只会显示一次。");
		REQUIRE_HOE_CONFIG_BY_LANGUAGE.put("zh_cn", "需要锄头");
		// gradle-auto-populated
	}
}