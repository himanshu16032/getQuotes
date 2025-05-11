package com.get_quotes.getQuotes.Service.Service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.springframework.stereotype.Component;
//import com.microsoft.playwright.*;
//import com.microsoft.playwright.options.*;
import com.gargoylesoftware.htmlunit.BrowserVersion;

import java.util.Arrays;
import java.util.List;

@Component
public class test {


    //    public  void action(){
//        String url = "https://www.myntra.com/kurtis/anouk/"
//                + "anouk-v-neck-regular-sleeves-short-kurti/28886594/buy";
//
//        try (Playwright playwright = Playwright.create()) {
//            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
//                    .setHeadless(true)
//                    .setArgs(Arrays.stream(new String[]{
//                            "--no-sandbox",
//                            "--disable-setuid-sandbox",
//                            "--disable-dev-shm-usage",
//                            "--disable-gpu"
//                    }).toList()));
//
//            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
//                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
//                            + "AppleWebKit/537.36 (KHTML, like Gecko) "
//                            + "Chrome/114.0.0.0 Safari/537.36")
//                    .setLocale("en-IN"));
//
//            Page page = context.newPage();
//
//            // Navigate and wait for network to be idle (all XHR/JS loaded)
//            page.navigate(url, new Page.NavigateOptions()
//                    .setWaitUntil(WaitUntilState.NETWORKIDLE)
//                    .setTimeout(60_000));
//
//            // Optionally, wait for the JSON-LD <script> to appear
//            page.waitForSelector("script[type='application/ld+json']", new Page.WaitForSelectorOptions()
//                    .setTimeout(20_000));
//
//            // Extract all JSON-LD blocks
//            System.out.println("=== JSON-LD Blocks ===");
//            for (ElementHandle tag : page.querySelectorAll("script[type='application/ld+json']")) {
//                String json = tag.innerText();
//                System.out.println(json);
//                System.out.println("----------------------");
//            }
//
//            // If you just want the full HTML:
//            // System.out.println(page.content());
//
//            page.close();
//            browser.close();
//        }
//    }
    public void action() {
        try (WebClient client = new WebClient(BrowserVersion.CHROME)) {
            client.getOptions().setJavaScriptEnabled(true);
            client.getOptions().setCssEnabled(false);
            client.getOptions().setThrowExceptionOnScriptError(false);
            HtmlPage page = client.getPage("https://www.myntra.com/kurtis/anouk/\"\n" +
                    "//                + \"anouk-v-neck-regular-sleeves-short-kurti/28886594/buy");
            client.waitForBackgroundJavaScript(5000);
            String html = page.asXml();
            System.out.println(html);
        }
        catch (Exception ex){

        }
    }
}

