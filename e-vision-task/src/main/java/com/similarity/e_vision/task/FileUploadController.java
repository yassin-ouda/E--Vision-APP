package com.similarity.e_vision.task;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Controller
public class FileUploadController {

    private final FileComparisonService fileComparisonService;

    public FileUploadController(FileComparisonService fileComparisonService) {
        this.fileComparisonService = fileComparisonService;
    }

    @RequestMapping("/")
    public String home() {
        return "uploadForm";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Model model) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/";
        }

        try {
            Path tempFile = Files.createTempFile("uploaded-", ".txt");
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            Map<String, Double> scores = fileComparisonService.compareFiles(tempFile);
            model.addAttribute("scores", scores);
            model.addAttribute("message", "File uploaded and compared successfully!");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "An error occurred while processing the file.");
            return "redirect:/";
        }

        return "result";
    }
}