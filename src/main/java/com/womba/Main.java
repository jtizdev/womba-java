package com.womba;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.util.Map;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * Womba Java CLI - Main entry point
 */
public class Main {
    private static final String VERSION = "1.0.0";

    public static void main(String[] args) {
        AnsiConsole.systemInstall();

        try {
            if (args.length == 0) {
                printUsage();
                System.exit(1);
            }

            String command = args[0];

            switch (command) {
                case "generate":
                    handleGenerate(args);
                    break;
                case "health":
                    handleHealth();
                    break;
                case "version":
                    handleVersion();
                    break;
                default:
                    System.out.println(ansi().fgRed().a("❌ Unknown command: " + command).reset());
                    printUsage();
                    System.exit(1);
            }
        } catch (Exception e) {
            System.out.println(ansi().fgRed().a("❌ Error: " + e.getMessage()).reset());
            System.exit(1);
        } finally {
            AnsiConsole.systemUninstall();
        }
    }

    private static void handleGenerate(String[] args) throws Exception {
        if (args.length < 3 || !args[1].equals("-story")) {
            System.out.println(ansi().fgRed().a("❌ Usage: womba generate -story <STORY-KEY> [--upload]").reset());
            System.exit(1);
        }

        String storyKey = args[2];
        boolean upload = args.length > 3 && args[3].equals("--upload");

        String apiUrl = System.getenv("WOMBA_API_URL");
        String apiKey = System.getenv("WOMBA_API_KEY");

        if (apiUrl == null || apiUrl.isEmpty()) {
            System.out.println(ansi().fgRed().a("❌ Error: WOMBA_API_URL environment variable not set").reset());
            System.out.println("\nSet it with: export WOMBA_API_URL=https://womba-api.up.railway.app");
            System.exit(1);
        }

        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println(ansi().fgRed().a("❌ Error: WOMBA_API_KEY environment variable not set").reset());
            System.out.println("\nSet it with: export WOMBA_API_KEY=your-api-key");
            System.exit(1);
        }

        System.out.println(ansi().fgCyan().a("🚀 Generating tests for " + storyKey + "...").reset());
        System.out.println();

        WombaClient client = new WombaClient(apiUrl, apiKey);
        WombaClient.GenerateResponse result = client.generateTests(storyKey, upload);

        // Print results
        String storyKeyResult = result.test_plan.story != null ? String.valueOf(result.test_plan.story.get("key")) : "N/A";
        System.out.println(ansi().fgGreen().a("✅ Successfully generated " + result.test_plan.test_cases.size() + " test cases for " + storyKeyResult + "!").reset());
        
        if (result.test_plan.metadata != null) {
            if (result.test_plan.metadata.containsKey("quality_score")) {
                System.out.println(ansi().fgCyan().a(String.format("📊 Quality Score: %.1f/100", ((Number) result.test_plan.metadata.get("quality_score")).doubleValue())).reset());
            }
            if (result.test_plan.metadata.containsKey("suggested_folder")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> folder = (Map<String, Object>) result.test_plan.metadata.get("suggested_folder");
                if (folder != null && folder.containsKey("name")) {
                    System.out.println(ansi().fgCyan().a("📁 Suggested Folder: " + folder.get("name")).reset());
                }
            }
            if (result.test_plan.metadata.containsKey("execution_time_seconds")) {
                System.out.println(ansi().fgCyan().a(String.format("⏱️  Execution Time: %.2fs", ((Number) result.test_plan.metadata.get("execution_time_seconds")).doubleValue())).reset());
            }
            if (result.test_plan.metadata.containsKey("ai_model")) {
                System.out.println(ansi().fgCyan().a("🤖 AI Model: " + result.test_plan.metadata.get("ai_model")).reset());
            }
        }

        // Print test cases
        System.out.println();
        System.out.println(ansi().fgYellow().a("Generated Test Cases:").reset());
        System.out.println(ansi().fgYellow().a("=".repeat(80)).reset());

        for (int i = 0; i < result.test_plan.test_cases.size(); i++) {
            WombaClient.TestCase testCase = result.test_plan.test_cases.get(i);
            System.out.println();
            System.out.println(ansi().fgCyan().a(String.format("%d. %s", i + 1, testCase.title)).reset());
            System.out.println("   Priority: " + testCase.priority + " | Type: " + testCase.test_type);
            System.out.println("   Description: " + testCase.description);
            System.out.println("   Steps: " + testCase.steps.size());
        }

        // Print Zephyr IDs if uploaded
        if (upload && result.zephyr_results != null && result.zephyr_results.containsKey("zephyr_ids")) {
            System.out.println();
            System.out.println(ansi().fgGreen().a("✅ Uploaded to Zephyr:").reset());
            @SuppressWarnings("unchecked")
            List<String> zephyrIds = (List<String>) result.zephyr_results.get("zephyr_ids");
            if (zephyrIds != null) {
                for (int i = 0; i < zephyrIds.size(); i++) {
                    System.out.println("   " + (i + 1) + ". " + zephyrIds.get(i));
                }
            }
        }

        System.out.println();
        System.out.println(ansi().fgGreen().a("🎉 Done!").reset());
    }

    private static void handleHealth() throws Exception {
        String apiUrl = System.getenv("WOMBA_API_URL");
        String apiKey = System.getenv("WOMBA_API_KEY");

        if (apiUrl == null || apiUrl.isEmpty()) {
            System.out.println(ansi().fgRed().a("❌ Error: WOMBA_API_URL environment variable not set").reset());
            System.exit(1);
        }

        System.out.println(ansi().fgCyan().a("🔍 Checking API health...").reset());
        System.out.println();

        WombaClient client = new WombaClient(apiUrl, apiKey != null ? apiKey : "");
        Map<String, Object> result = client.healthCheck();

        System.out.println(ansi().fgGreen().a("✅ API is healthy!").reset());
        System.out.println("Status: " + result.get("status"));
        System.out.println("Version: " + result.get("version"));

        if (result.containsKey("dependencies")) {
            System.out.println("\nDependencies:");
            @SuppressWarnings("unchecked")
            Map<String, Object> deps = (Map<String, Object>) result.get("dependencies");
            for (Map.Entry<String, Object> entry : deps.entrySet()) {
                String status = entry.getValue().toString();
                if ("connected".equals(status)) {
                    System.out.println(ansi().fgGreen().a("  ✅ " + entry.getKey() + ": " + status).reset());
                } else {
                    System.out.println(ansi().fgYellow().a("  ⚠️  " + entry.getKey() + ": " + status).reset());
                }
            }
        }
    }

    private static void handleVersion() {
        System.out.println(ansi().fgCyan().a("Womba Java CLI v" + VERSION).reset());
    }

    private static void printUsage() {
        System.out.println("Womba CLI - Java Client");
        System.out.println("\nUsage:");
        System.out.println("  java -jar womba.jar <command> [options]");
        System.out.println("\nCommands:");
        System.out.println("  generate    Generate test cases for a Jira story");
        System.out.println("  health      Check API health");
        System.out.println("  version     Show version");
        System.out.println("\nExamples:");
        System.out.println("  java -jar womba.jar generate -story PLAT-12991");
        System.out.println("  java -jar womba.jar generate -story PLAT-12991 --upload");
        System.out.println("  java -jar womba.jar health");
        System.out.println("\nEnvironment Variables:");
        System.out.println("  WOMBA_API_URL    Womba API base URL");
        System.out.println("  WOMBA_API_KEY    Womba API authentication key");
    }
}

