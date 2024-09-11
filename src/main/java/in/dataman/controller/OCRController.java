// package in.dataman.controller;

// import net.sourceforge.tess4j.Tesseract;
// import net.sourceforge.tess4j.TesseractException;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.core.io.Resource;
// import org.springframework.core.io.ResourceLoader;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;

// import jakarta.annotation.PostConstruct;

// import javax.imageio.ImageIO;
// import java.awt.image.BufferedImage;
// import java.io.ByteArrayInputStream;
// import java.io.File;
// import java.io.IOException;
// import java.io.InputStream;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

// @RestController
// @RequestMapping("/api/ocr")
// public class OCRController {

//     private Tesseract tesseract;

//     @Autowired
//     private ResourceLoader resourceLoader;

//     public OCRController() {
//         tesseract = new Tesseract();
//         tesseract.setLanguage("eng"); // Set the language
//     }


//     @GetMapping("/")
//     public String check(){

//         return "jai shree krishna";
//     }
    
//     @PostConstruct
//     public void init() throws IOException {
//         String herokuTessdataPath = "/app/vendor/tesseract-ocr/tessdata";  // Path for Heroku

//         // Check if the app is running on Heroku by checking for a specific environment variable
//         String dyno = System.getenv("DYNO");
        
//         if (dyno != null) {
//             // App is running on Heroku
//             System.out.println("Running on Heroku, using Heroku tessdata path: " + herokuTessdataPath);
//             tesseract.setDatapath(herokuTessdataPath);  // Set the Heroku tessdata path
//         } else {
//             // App is running locally, load tessdata from resources folder
//             System.out.println("Running locally, loading tessdata from classpath.");
//             Resource resource = resourceLoader.getResource("classpath:tessdata");
//             File tessDataFolder = resource.getFile();
//             tesseract.setDatapath(tessDataFolder.getAbsolutePath());  // Set the local tessdata path
//         }
//     }

//     @PostMapping("/upload")
//     public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
//         if (file.isEmpty()) {
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file uploaded");
//         }

//         try {
//             byte[] imageBytes = file.getBytes();
//             String extractedText = extractTextFromImage(imageBytes);

//             Map<String, String> parsedInfo = new HashMap<>();
//             parsedInfo.put("aadhar_number", parseAadharNumber(extractedText));
//             parsedInfo.put("name", parseName(extractedText));
//             parsedInfo.put("dob", parseDOB(extractedText));
//             parsedInfo.put("gender", parseGender(extractedText));

//             return ResponseEntity.ok(parsedInfo);

//         } catch (IOException | TesseractException e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .body("Error processing the image: " + e.getMessage());
//         }
//     }

//     private String extractTextFromImage(byte[] imageBytes) throws TesseractException, IOException {
//         InputStream bis = new ByteArrayInputStream(imageBytes);
//         BufferedImage bufferedImage = ImageIO.read(bis);

//         if (bufferedImage == null) {
//             throw new IOException("Unsupported image format");
//         }

//         return tesseract.doOCR(bufferedImage);
//     }

//     private String parseAadharNumber(String text) {
//         String aadharPattern = "\\d{4}\\s*\\d{4}\\s*\\d{4}";
//         Pattern pattern = Pattern.compile(aadharPattern);
//         Matcher matcher = pattern.matcher(text);

//         if (matcher.find()) {
//             return matcher.group().replaceAll("\\s+", "");
//         }
//         return "Aadhar number not found";
//     }

//     private String parseName(String text) {
//         String[] namePatterns = { "Name[:\\s]*([A-Za-z\\s]+)", "Name\\s*[:\\s]*([A-Za-z\\s]+)",
//                 "Name:\\s*([A-Za-z\\s]+)", "Name\\s*([A-Za-z\\s]+)", "([A-Z][a-z]+(?:\\s[A-Z][a-z]+)*)" };

//         for (String namePattern : namePatterns) {
//             Pattern pattern = Pattern.compile(namePattern, Pattern.CASE_INSENSITIVE);
//             Matcher matcher = pattern.matcher(text);

//             if (matcher.find()) {
//                 return matcher.group(1).trim();
//             }
//         }

//         return "Name not found";
//     }

//     private String parseDOB(String text) {
//         String dobPattern = "DOB[:\\s]*(\\d{2}/\\d{2}/\\d{4})";
//         Pattern pattern = Pattern.compile(dobPattern);
//         Matcher matcher = pattern.matcher(text);

//         if (matcher.find()) {
//             return matcher.group(1).trim();
//         }
//         return "Date of Birth not found";
//     }

//     private String parseGender(String text) {
//         String genderPattern = "(Male|Female|MALE|FEMALE)";
//         Pattern pattern = Pattern.compile(genderPattern, Pattern.CASE_INSENSITIVE);
//         Matcher matcher = pattern.matcher(text);

//         if (matcher.find()) {
//             return matcher.group(1).toUpperCase();
//         }
//         return "Gender not found";
//     }
// }




package in.dataman.controller;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/ocr")
public class OCRController {

    private Tesseract tesseract;

    @Autowired
    private ResourceLoader resourceLoader;

    public OCRController() {
        tesseract = new Tesseract();
        tesseract.setLanguage("eng"); // Set the language
    }

    @PostConstruct
    public void init() throws IOException {
        String herokuTessdataPath = "/src/main/resources/tessdata";  // Path for Heroku

        // Check if the app is running on Heroku by checking for a specific environment variable
        String dyno = System.getenv("DYNO");

        if (dyno != null) {
            // App is running on Heroku
            System.out.println("Running on Heroku, using Heroku tessdata path: " + herokuTessdataPath);
            tesseract.setDatapath(herokuTessdataPath);  // Set the Heroku tessdata path
        } else {
            // App is running locally, load tessdata from resources folder
            System.out.println("Running locally, loading tessdata from classpath.");
            Resource resource = resourceLoader.getResource("classpath:tessdata");
            File tessDataFolder = resource.getFile();
            tesseract.setDatapath(tessDataFolder.getAbsolutePath());  // Set the local tessdata path
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file uploaded");
        }

        try {
            byte[] imageBytes = file.getBytes();
            String extractedText = extractTextFromImage(imageBytes);
            System.out.println(extractedText);

            Map<String, String> parsedInfo = new HashMap<>();
            parsedInfo.put("aadhar_number", parseAadharNumber(extractedText));
            parsedInfo.put("name", parseName(extractedText));
            parsedInfo.put("dob", parseDOB(extractedText));
            parsedInfo.put("gender", parseGender(extractedText));

            return ResponseEntity.ok(parsedInfo);

        } catch (IOException | TesseractException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing the image: " + e.getMessage());
        }
    }

    private String extractTextFromImage(byte[] imageBytes) throws TesseractException, IOException {
        InputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(bis);

        if (bufferedImage == null) {
            throw new IOException("Unsupported image format");
        }

        return tesseract.doOCR(bufferedImage);
    }

    private String parseAadharNumber(String text) {
        String aadharPattern = "\\d{4}\\s*\\d{4}\\s*\\d{4}";
        Pattern pattern = Pattern.compile(aadharPattern);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group().replaceAll("\\s+", "");
        }
        return "Aadhar number not found";
    }

    private String parseName(String text) {
        String[] namePatterns = { "Name[:\\s]*([A-Za-z\\s]+)", "Name\\s*[:\\s]*([A-Za-z\\s]+)",
                "Name:\\s*([A-Za-z\\s]+)", "Name\\s*([A-Za-z\\s]+)", "([A-Z][a-z]+(?:\\s[A-Z][a-z]+)*)" };

        for (String namePattern : namePatterns) {
            Pattern pattern = Pattern.compile(namePattern, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }

        return "Name not found";
    }

    private String parseDOB(String text) {
        String dobPattern = "DOB[:\\s]*(\\d{2}/\\d{2}/\\d{4})";
        Pattern pattern = Pattern.compile(dobPattern);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Date of Birth not found";
    }

    private String parseGender(String text) {
        String genderPattern = "(Male|Female|MALE|FEMALE)";
        Pattern pattern = Pattern.compile(genderPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1).toUpperCase();
        }
        return "Gender not found";
    }
}

