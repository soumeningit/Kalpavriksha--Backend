package com.soumen.kalpavriksha.Utills;

import org.jsoup.safety.Safelist;

import org.jsoup.Jsoup;
import java.util.Set;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Cleaner;

import java.nio.charset.StandardCharsets;

public class JsoupHtmlSanitizer
{
    private static final Set<String> FORBIDDEN_TAGS_SAMPLE =
            Set.of("script", "style", "iframe", "object", "embed", "svg", "math", "video", "audio", "source");

    // Strict, minimal allowlist
    private static final Safelist STRICT_LIST = new Safelist()
            // Allowed tags (minimal formatting + lists + code + small headings + links + images)
            .addTags("p", "br",
                    "ul", "ol", "li",
                    "b", "strong", "i", "em", "u",
                    "blockquote", "code", "pre",
                    "h1", "h2", "h3",
                    "a", "img")
            // Allowed attributes
            .addAttributes("a", "href", "title", "target", "rel")
            .addAttributes("img", "src", "alt")
            // Enforce safe protocols
            .addProtocols("a", "href", "http", "https", "mailto")
            .addProtocols("img", "src", "https")
            // Enforce safe attributes on links (prevents tab-nabbing)
            .addEnforcedAttribute("a", "rel", "noopener noreferrer")
            .addEnforcedAttribute("a", "target", "_blank")
            // Optional: reduce referrer leakage on images
            .addEnforcedAttribute("img", "referrerpolicy", "no-referrer");

    static {
        // Disallow relative links; everything must be absolute http(s)
        STRICT_LIST.preserveRelativeLinks(false);
    }

    private JsoupHtmlSanitizer() {}

    /**
     * Extreme sanitize:
     * - Only strict allowlist tags/attrs
     * - No inline styles/classes/ids
     * - No data: URIs (base64 images/JS)
     * - Only http/https/mailto links
     * - Only https images
     * - Output length capped (defense-in-depth)
     */
    public static String sanitize(String html, int maxOutputLength)
    {
        if (html == null || html.isBlank()) return "";

        // Quick hard-fail checks (optional: log & proceed; here we just log intention)
        String lower = html.toLowerCase();
        for (String tag : FORBIDDEN_TAGS_SAMPLE) {
            if (lower.contains("<" + tag)) {
                 // log.warn("Forbidden tag present: {}", tag);
                // Jsoup will remove it; we just note it.
            }
        }
        if (lower.contains("data:")) {
            // log.warn("data: URI detected. Will be removed.");
        }
        if (lower.contains("onerror=") || lower.contains("onload=") || lower.matches(".*on[a-z]+\\s*=.*")) {
            // log.warn("Inline JS handler detected. Will be removed.");
        }

        // Parse with safe defaults
        Document dirty = Jsoup.parse(html, "", org.jsoup.parser.Parser.htmlParser());
        dirty.outputSettings()
                .escapeMode(Entities.EscapeMode.base)
                .charset(StandardCharsets.UTF_8)
                .prettyPrint(false);

        // Clean using allowlist
        Cleaner cleaner = new Cleaner(STRICT_LIST);
        Document clean = cleaner.clean(dirty);

        // Remove any attributes we never want (defense in depth)
        clean.select("*").forEach(el -> {
            // Strip style/class/id universally
            el.removeAttr("style");
            el.removeAttr("class");
            el.removeAttr("id");

            // Strip javascript: and data: even if somehow survived (belt & suspenders)
            if (el.hasAttr("href")) {
                String href = el.attr("href").trim();
                if (href.startsWith("javascript:") || href.startsWith("data:")) {
                    el.removeAttr("href");
                }
            }
            if (el.tagName().equals("img")) {
                String src = el.attr("src").trim();
                // Only allow https images; block http and data/base64
                if (!src.startsWith("https://")) {
                    el.removeAttr("src");
                }
            }
        });

        // Remove empty elements that add no value (optional tidy)
        clean.select("p:matches(^\\s*$), h1:matches(^\\s*$), h2:matches(^\\s*$), h3:matches(^\\s*$), li:matches(^\\s*$)")
                .remove();

        // Get sanitized inner HTML (body only)
        String safe = clean.body().html().trim();

        // Cap output size (prevents DB bloat / abuse)
        int limit = Math.max(0, maxOutputLength);
        if (limit > 0 && safe.length() > limit) {
            safe = safe.substring(0, limit) + "...";
        }
        return safe;
    }

    /**
     * Maximum safety: strip EVERYTHING to plain text.
     */
    public static String toPlainText(String html, int maxLength)
    {
        if (html == null || html.isBlank()) return "";
        String text = Jsoup.parse(html).text().replaceAll("\\s+", " ").trim();
        if (maxLength > 0 && text.length() > maxLength) {
            return text.substring(0, maxLength) + "...";
        }
        return text;
    }
}

