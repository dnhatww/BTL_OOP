package com.tailieuptit.demo.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtil {

    // Pattern để loại bỏ dấu (diacritics)
    private static final Pattern DIACRITICS_STRIPPER = Pattern.compile("\\p{M}");

    // Pattern để thay thế khoảng trắng
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");

    // Pattern để loại bỏ các ký tự không phải chữ/số/gạch nối
    private static final Pattern INVALID_CHARS = Pattern.compile("[^a-z0-9-]");

    // Pattern để thay thế nhiều gạch nối liên tiếp
    private static final Pattern MULTI_DASH = Pattern.compile("-{2,}");

    // Pattern để xóa gạch nối ở đầu/cuối
    private static final Pattern EDGE_DASHES = Pattern.compile("^-|-$");

    /**
     * Chuyển đổi một chuỗi bất kỳ thành một chuỗi "slug" an toàn cho URL.
     * Ví dụ: "Tài liệu Lập Trình Java (Nâng cao)" -> "tai-lieu-lap-trinh-java-nang-cao"
     *
     * @param input Chuỗi đầu vào
     * @return Chuỗi slug
     */
    public static String toSlug(String input) {
        if (input == null) {
            return "";
        }

        // 1. Chuẩn hóa NFD (Normalized Form Decomposed) để tách dấu
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        // 2. Dùng regex loại bỏ các dấu đã tách
        String noAccent = DIACRITICS_STRIPPER.matcher(normalized).replaceAll("");

        // 3. Xử lý 'đ' và 'Đ' (vì Normalizer không xử lý)
        noAccent = noAccent.replaceAll("đ", "d").replaceAll("Đ", "D");

        // 4. Chuyển sang chữ thường
        String lower = noAccent.toLowerCase(Locale.ENGLISH);

        // 5. Thay thế khoảng trắng bằng gạch nối
        String withDashes = WHITESPACE.matcher(lower).replaceAll("-");

        // 6. Loại bỏ tất cả các ký tự không hợp lệ
        String validChars = INVALID_CHARS.matcher(withDashes).replaceAll("");

        // 7. Thay thế nhiều gạch nối bằng một gạch nối
        String singleDash = MULTI_DASH.matcher(validChars).replaceAll("-");

        // 8. Xóa gạch nối ở đầu/cuối
        String slug = EDGE_DASHES.matcher(singleDash).replaceAll("");

        return slug;
    }
}