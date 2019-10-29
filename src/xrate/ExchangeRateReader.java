package xrate;

import java.io.*;
import java.net.URL;
import java.util.Properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * Provide access to basic currency exchange rate services.
 */
public class ExchangeRateReader {

    private String accessKey;
    private String url;
    /**
     * Construct an exchange rate reader using the given base URL. All requests
     * will then be relative to that URL. If, for example, your source is Xavier
     * Finance, the base URL is http://api.finance.xaviermedia.com/api/ Rates
     * for specific days will be constructed from that URL by appending the
     * year, month, and day; the URL for 25 June 2010, for example, would be
     * http://api.finance.xaviermedia.com/api/2010/06/25.xml
     * 
     * @param baseURL
     *            the base URL for requests
     */
    public ExchangeRateReader(String baseURL) throws IOException {
        /*
         * DON'T DO MUCH HERE!
         * People often try to do a lot here, but the action is actually in
         * the two methods below. All you need to do here is store the
         * provided `baseURL` in a field so it will be accessible later.
         */

        // TODO Your code here
        this.url = baseURL;
        // Reads the access keys from `etc/access_keys.properties`
        readAccessKeys();
    }

    /**
     * This reads the `fixer_io` access key from `etc/access_keys.properties`
     * and assigns it to the field `accessKey`.
     *
     * @throws IOException if there is a problem reading the properties file
     */
    private void readAccessKeys() throws IOException {
        Properties properties = new Properties();
        FileInputStream in = null;
        try {
            // Don't change this filename unless you know what you're doing.
            // It's crucial that we don't commit the file that contains the
            // (private) access keys. This file is listed in `.gitignore` so
            // it's safe to put keys there as we won't accidentally commit them.
            in = new FileInputStream("etc/access_keys.properties");
        } catch (FileNotFoundException e) {
            /*
             * If this error gets generated, make sure that you have the desired
             * properties file in your project's `etc` directory. You may need
             * to rename the file ending in `.sample` by removing that suffix.
             */
            System.err.println("Couldn't open etc/access_keys.properties; have you renamed the sample file?");
            throw(e);
        }
        properties.load(in);
        // This assumes we're using Fixer.io and that the desired access key is
        // in the properties file in the key labelled `fixer_io`.
        accessKey = properties.getProperty("fixer_io");
    }

    /**
     * Get the exchange rate for the specified currency against the base
     * currency (the Euro) on the specified date.
     * 
     * @param currencyCode
     *            the currency code for the desired currency
     * @param year
     *            the year as a four digit integer
     * @param month
     *            the month as an integer (1=Jan, 12=Dec)
     * @param day
     *            the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException if there are problems reading from the server
     */
    public float actuallyGetExchangeRate(String fromCode, String yearString, String monthString, String dayString)  throws IOException {
         return actuallyGetExchangeRate(fromCode, "EUR", yearString, monthString, dayString);
    }

    public float actuallyGetExchangeRate(String fromCode, String toCode, String yearString, String monthString, String dayString)  throws IOException {
        float exchangeFrom;
        float exchangeTo;
        float exchangeRate = 0;
        if (monthString.length() < 2) {
            monthString = "0" + monthString;
        }
        if (dayString.length() < 2) {
            dayString = "0" + dayString;
        }
        String uS = this.url + yearString + "-" + monthString + "-" + dayString + "?" + "access_key=" + accessKey;
        URL url = new URL(uS);
        InputStream inputStream = url.openStream();
        JsonParser j = new JsonParser();
        JsonObject jO = (JsonObject) j.parse(new InputStreamReader(inputStream));
        JsonObject rates = jO.getAsJsonObject("rates");
        exchangeFrom = rates.getAsJsonPrimitive(fromCode).getAsFloat();
        exchangeTo = rates.getAsJsonPrimitive(toCode).getAsFloat();
        System.out.println(exchangeFrom);
        System.out.println(exchangeTo);

        System.out.println("exchangeRate: " + exchangeFrom);
        inputStream.close();
        return exchangeFrom/exchangeTo;
    }
    public float getExchangeRate(String currencyCode, int year, int month, int day) throws IOException {
        // TODO Your code here
        String yearString = Integer.toString(year);
        String monthString = Integer.toString(month);
        String dayString = Integer.toString(day);
        return actuallyGetExchangeRate(currencyCode, yearString, monthString, dayString);
    }

    /**
     * Get the exchange rate of the first specified currency against the second
     * on the specified date.
     * 
     * @param fromCurrency
     *            the currency code we're exchanging *from*
     * @param toCurrency
     *            the currency code we're exchanging *to*
     * @param year
     *            the year as a four digit integer
     * @param month
     *            the month as an integer (1=Jan, 12=Dec)
     * @param day
     *            the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException if there are problems reading from the server
     */
    public float getExchangeRate(
            String fromCurrency, String toCurrency,
            int year, int month, int day) throws IOException {
        // TODO Your code here
        String yearString = Integer.toString(year);
        String monthString = Integer.toString(month);
        String dayString = Integer.toString(day);
        return actuallyGetExchangeRate(fromCurrency, toCurrency, yearString, monthString, dayString);
    }
}