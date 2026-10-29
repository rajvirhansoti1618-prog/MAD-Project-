package com.example.lexidesk

import com.example.lexidesk.utils.ValidationUtils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun isValid10DigitPhone_withValid10Digits_returnsTrue() {
        assertTrue(ValidationUtils.isValid10DigitPhone("9876543210"))
        assertTrue(ValidationUtils.isValid10DigitPhone("1234567890"))
        assertTrue(ValidationUtils.isValid10DigitPhone("0123456789"))
    }

    @Test
    fun isValid10DigitPhone_withInvalidPhone_returnsFalse() {
        // Less than 10 digits
        assertFalse(ValidationUtils.isValid10DigitPhone("123456789"))
        assertFalse(ValidationUtils.isValid10DigitPhone("98765"))
        assertFalse(ValidationUtils.isValid10DigitPhone(""))
        assertFalse(ValidationUtils.isValid10DigitPhone(null))
        assertFalse(ValidationUtils.isValid10DigitPhone("          "))

        // More than 10 digits
        assertFalse(ValidationUtils.isValid10DigitPhone("98765432101"))
        assertFalse(ValidationUtils.isValid10DigitPhone("123456789012345"))

        // Non-digit characters, spaces, letters, or symbols
        assertFalse(ValidationUtils.isValid10DigitPhone("987654321a"))
        assertFalse(ValidationUtils.isValid10DigitPhone("98765 43210"))
        assertFalse(ValidationUtils.isValid10DigitPhone("+9198765432"))
        assertFalse(ValidationUtils.isValid10DigitPhone("98765-43210"))
        assertFalse(ValidationUtils.isValid10DigitPhone("(987)654321"))
    }

    @Test
    fun isValidPhone_withValidDigits_returnsTrue() {
        assertTrue(ValidationUtils.isValidPhone("1234567"))
        assertTrue(ValidationUtils.isValidPhone("9876543210"))
        assertTrue(ValidationUtils.isValidPhone("1234567890123"))
    }

    @Test
    fun isValidPhone_withInternationalAndDelimiters_returnsTrue() {
        assertTrue(ValidationUtils.isValidPhone("+919876543210"))
        assertTrue(ValidationUtils.isValidPhone("+1 555-0199"))
        assertTrue(ValidationUtils.isValidPhone("+44 20 7946 0919"))
        assertTrue(ValidationUtils.isValidPhone("022-26543210"))
    }

    @Test
    fun isValidPhone_withShortLength_returnsFalse() {
        assertFalse(ValidationUtils.isValidPhone("123456"))
        assertFalse(ValidationUtils.isValidPhone("1"))
    }

    @Test
    fun isValidPhone_withNonDigits_returnsFalse() {
        assertFalse(ValidationUtils.isValidPhone("12345abc"))
        assertFalse(ValidationUtils.isValidPhone("phone-number"))
    }

    @Test
    fun isValidEmailFormat_withValidEmail_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmailFormat("test@example.com"))
        assertTrue(ValidationUtils.isValidEmailFormat("advocate.sharma@lawfirm.co.in"))
        assertTrue(ValidationUtils.isValidEmailFormat("audit.team_123@corporation.org"))
        assertTrue(ValidationUtils.isValidEmail("test@example.com"))
    }

    @Test
    fun isValidEmailFormat_withInvalidEmail_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmailFormat("invalid-email"))
        assertFalse(ValidationUtils.isValidEmailFormat("@domain.com"))
        assertFalse(ValidationUtils.isValidEmailFormat("user@.com"))
        assertFalse(ValidationUtils.isValidEmailFormat(null))
        assertFalse(ValidationUtils.isValidEmailFormat(""))
        assertFalse(ValidationUtils.isValidEmail(null))
        assertFalse(ValidationUtils.isValidEmail(""))
    }

    @Test
    fun isValidEmail_rejectsCommonInvalidFormats() {
        // Explicitly tests: abc, abc@, abc.com, abc@domain
        assertFalse(ValidationUtils.isValidEmail("abc"))
        assertFalse(ValidationUtils.isValidEmail("abc@"))
        assertFalse(ValidationUtils.isValidEmail("abc.com"))
        assertFalse(ValidationUtils.isValidEmail("abc@domain"))
        assertFalse(ValidationUtils.isValidEmailFormat("abc"))
        assertFalse(ValidationUtils.isValidEmailFormat("abc@"))
        assertFalse(ValidationUtils.isValidEmailFormat("abc.com"))
        assertFalse(ValidationUtils.isValidEmailFormat("abc@domain"))
    }

    @Test
    fun isValidTitle_withValidString_returnsTrue() {
        assertTrue(ValidationUtils.isValidTitle("Client Case", 2))
        assertTrue(ValidationUtils.isValidTitle("Tax Audit", 3))
    }

    @Test
    fun isValidTitle_withShortOrBlank_returnsFalse() {
        assertFalse(ValidationUtils.isValidTitle("a", 2))
        assertFalse(ValidationUtils.isValidTitle("", 2))
        assertFalse(ValidationUtils.isValidTitle("   ", 2))
        assertFalse(ValidationUtils.isValidTitle(null, 2))
    }

    @Test
    fun isValidTitle_withWhitespacePadding_trimsCorrectly() {
        assertTrue(ValidationUtils.isValidTitle("   ab   ", 2))
        assertFalse(ValidationUtils.isValidTitle("   a   ", 2))
        assertTrue(ValidationUtils.isValidTitle("M/s ABC Corp v. State (2026)", 5))
    }

    @Test
    fun isValidEmailFormat_withSubdomains_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmailFormat("partner@mail.delhi.lawfirm.in"))
    }

    @Test
    fun isValidPostalCode_withValidCodes_returnsTrue() {
        assertTrue(ValidationUtils.isValidPostalCode("90210"))
        assertTrue(ValidationUtils.isValidPostalCode("110001"))
        assertTrue(ValidationUtils.isValidPostalCode("SW1A 1AA"))
        assertTrue(ValidationUtils.isValidPostalCode("K1A-0B1"))
        assertTrue(ValidationUtils.isValidPostalCode("100-0001"))
    }

    @Test
    fun isValidPostalCode_withInvalidCodes_returnsFalse() {
        assertFalse(ValidationUtils.isValidPostalCode(""))
        assertFalse(ValidationUtils.isValidPostalCode(null))
        assertFalse(ValidationUtils.isValidPostalCode("12"))
        assertFalse(ValidationUtils.isValidPostalCode("1234567890123"))
        assertFalse(ValidationUtils.isValidPostalCode("90210#@!"))
    }

    @Test
    fun isValidUrl_withValidHttpUrls_returnsTrue() {
        assertTrue(ValidationUtils.isValidUrl("http://example.com"))
        assertTrue(ValidationUtils.isValidUrl("https://example.com"))
        assertTrue(ValidationUtils.isValidUrl("https://www.example.com/path?query=1"))
        assertTrue(ValidationUtils.isValidUrl("https://sub.domain.co.in/page#section"))
    }

    @Test
    fun isValidUrl_withInvalidUrls_returnsFalse() {
        assertFalse(ValidationUtils.isValidUrl("ftp://example.com"))
        assertFalse(ValidationUtils.isValidUrl("example.com"))
        assertFalse(ValidationUtils.isValidUrl("not a url"))
        assertFalse(ValidationUtils.isValidUrl(""))
        assertFalse(ValidationUtils.isValidUrl(null))
        assertFalse(ValidationUtils.isValidUrl("   "))
    }

    @Test
    fun isValidUrl_withWhitespacePadding_trimsCorrectly() {
        assertTrue(ValidationUtils.isValidUrl("  https://example.com  "))
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Edge-case tests added for stricter phone/email validation (Oct 2026)
    // ────────────────────────────────────────────────────────────────────────────

    /** isValid10DigitPhone — Unicode / non-ASCII digit rejection */
    @Test
    fun isValid10DigitPhone_rejectsUnicodeAndNonAsciiDigits() {
        // Arabic-Indic digits — Char.isDigit() returns true for these, but ASCII range check must not
        assertFalse(ValidationUtils.isValid10DigitPhone("٩٨٧٦٥٤٣٢١٠"))
        // Circled digit characters (e.g. ①②③…)
        assertFalse(ValidationUtils.isValid10DigitPhone("①②③④⑤⑥⑦⑧⑨⓪"))
    }

    /** isValid10DigitPhone — mixed-valid-looking inputs */
    @Test
    fun isValid10DigitPhone_rejectsMixedValidLooking() {
        // 11 chars with leading '+' — stripping would change the number
        assertFalse(ValidationUtils.isValid10DigitPhone("+9198765432"))
        // Trailing space — trim() is applied internally so "9198765432 " trims to 10 digits → true
        // (space trimmed → valid 10 digits, consistent with existing trim behavior)
        assertTrue(ValidationUtils.isValid10DigitPhone("9198765432 "))
        // Embedded space → not all chars in ASCII digit range after trim → false
        assertFalse(ValidationUtils.isValid10DigitPhone("9198 76543"))
        // Leading/trailing zeros (valid 10-digit number)
        assertTrue(ValidationUtils.isValid10DigitPhone("0000000000"))
    }

    /** isValid10DigitPhone — rejects internal symbols even when digit-count is 10 */
    @Test
    fun isValid10DigitPhone_rejectsSymbolsEvenWithCorrectDigitCount() {
        assertFalse(ValidationUtils.isValid10DigitPhone("919876-432"))  // hyphen, only 9 digits
        assertFalse(ValidationUtils.isValid10DigitPhone("91987+6432"))  // plus sign inside
        assertFalse(ValidationUtils.isValid10DigitPhone("9198764321a")) // letter suffix → 11 chars
    }

    /** isValidEmailFormat — consecutive dots in local part or domain */
    @Test
    fun isValidEmailFormat_rejectsConsecutiveDots() {
        assertFalse(ValidationUtils.isValidEmailFormat("a..b@example.com"))   // consecutive dots in local
        assertFalse(ValidationUtils.isValidEmailFormat("a@b..com"))           // consecutive dots in domain
        assertFalse(ValidationUtils.isValidEmailFormat("a@ex..ample.com"))    // consecutive dots mid-domain
    }

    /** isValidEmailFormat — leading or trailing dot in local part */
    @Test
    fun isValidEmailFormat_rejectsLeadingTrailingDotInLocal() {
        assertFalse(ValidationUtils.isValidEmailFormat(".a@example.com"))   // starts with dot
        assertFalse(ValidationUtils.isValidEmailFormat("a.@example.com"))   // ends with dot
        assertFalse(ValidationUtils.isValidEmailFormat(".@example.com"))    // only a dot
    }

    /** isValidEmailFormat — domain labels starting or ending with a hyphen */
    @Test
    fun isValidEmailFormat_rejectsDomainLabelStartingOrEndingWithHyphen() {
        assertFalse(ValidationUtils.isValidEmailFormat("a@-example.com"))   // label starts with hyphen
        assertFalse(ValidationUtils.isValidEmailFormat("a@example-.com"))   // label ends with hyphen
        assertFalse(ValidationUtils.isValidEmailFormat("a@ex.-ample.com"))  // subdomain starts with hyphen
        assertFalse(ValidationUtils.isValidEmailFormat("a@ex.ample-.co"))   // subdomain ends with hyphen
    }

    /** isValidEmailFormat — valid subdomains and co.in style addresses */
    @Test
    fun isValidEmailFormat_acceptsValidSubdomainsAndCoIn() {
        assertTrue(ValidationUtils.isValidEmailFormat("name@company.co.in"))
        assertTrue(ValidationUtils.isValidEmailFormat("user@mail.sub.domain.org"))
        assertTrue(ValidationUtils.isValidEmailFormat("a@b.com"))          // single-char local
        assertTrue(ValidationUtils.isValidEmailFormat("x.y@example.com"))  // dot in local (not leading/trailing)
        assertTrue(ValidationUtils.isValidEmailFormat("first.last+tag@sub.domain.io"))
    }
}
