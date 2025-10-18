# Womba Java CLI

Java client for the Womba AI test generation service.

## Features

- ☕ Pure Java 11+ implementation
- 🎨 Colored terminal output
- 📦 Single JAR with all dependencies
- 🔒 Secure API authentication
- 🚀 Fast HTTP client

## Installation

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher (for building from source)

### Option 1: Download Pre-built JAR

Download the latest `womba.jar` from [Releases](https://github.com/jtizdev/womba-java/releases).

### Option 2: Build from Source

```bash
git clone https://github.com/jtizdev/womba-java.git
cd womba-java
mvn clean package
```

The executable JAR will be in `target/womba.jar`.

## Configuration

Set environment variables:

```bash
# Required
export WOMBA_API_URL="https://womba-api.up.railway.app"
export WOMBA_API_KEY="your-api-key-here"

# Add to ~/.bashrc or ~/.zshrc for persistence
echo 'export WOMBA_API_URL="https://womba-api.up.railway.app"' >> ~/.bashrc
echo 'export WOMBA_API_KEY="your-api-key-here"' >> ~/.bashrc
source ~/.bashrc
```

## Usage

### Generate Tests

Generate test cases for a Jira story:

```bash
# Basic generation (no upload)
java -jar womba.jar generate -story PLAT-12991

# Generate and upload to Zephyr
java -jar womba.jar generate -story PLAT-12991 --upload
```

### Check API Health

```bash
java -jar womba.jar health
```

### Show Version

```bash
java -jar womba.jar version
```

## Example Output

```
🚀 Generating tests for PLAT-12991...

✅ Successfully generated 8 test cases!
📊 Quality Score: 88.5/100
📁 Suggested Folder: Orchestration WS/POP ID Alignment
⏱️  Execution Time: 12.34s
🤖 AI Model: gpt-4o

Generated Test Cases:
================================================================================

1. Verify POP ID alignment in orchestration workflow
   Priority: High | Type: Functional
   Description: Test that POP IDs are correctly aligned...
   Steps: 5

2. Test POP ID validation with invalid inputs
   Priority: High | Type: Negative
   Description: Verify system handles invalid POP IDs...
   Steps: 4

...

🎉 Done!
```

## Commands

| Command | Description | Example |
|---------|-------------|---------|
| `generate` | Generate test cases | `java -jar womba.jar generate -story PLAT-12991` |
| `health` | Check API status | `java -jar womba.jar health` |
| `version` | Show CLI version | `java -jar womba.jar version` |

## Flags

### generate

- `-story` (required) - Jira story key (e.g., PLAT-12991)
- `--upload` (optional) - Upload generated tests to Zephyr

## Development

### Build

```bash
# Clean and build
mvn clean package

# Run tests
mvn test

# Install to local Maven repository
mvn install
```

### Project Structure

```
womba-java/
├── pom.xml                        # Maven configuration
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── womba/
│                   ├── Main.java          # CLI entry point
│                   └── WombaClient.java   # HTTP client
└── README.md                      # This file
```

### Dependencies

- **Gson** (2.10.1) - JSON parsing
- **Jansi** (2.4.0) - Colored console output
- **JUnit** (5.10.1) - Testing (test scope)

## Architecture

```
┌─────────────┐
│  Java CLI   │
│(womba-java) │
└──────┬──────┘
       │ HTTP
       ↓
┌─────────────┐
│  Womba API  │
│  (Python)   │
└─────────────┘
```

The Java CLI is a thin wrapper that calls the Womba API service via HTTP. All test generation logic is in the Python service.

## Integration with CI/CD

### Jenkins

```groovy
pipeline {
    agent any
    environment {
        WOMBA_API_URL = 'https://womba-api.up.railway.app'
        WOMBA_API_KEY = credentials('womba-api-key')
    }
    stages {
        stage('Generate Tests') {
            steps {
                sh 'java -jar womba.jar generate -story ${JIRA_STORY_KEY}'
            }
        }
    }
}
```

### GitHub Actions

```yaml
- name: Generate Womba Tests
  env:
    WOMBA_API_URL: https://womba-api.up.railway.app
    WOMBA_API_KEY: ${{ secrets.WOMBA_API_KEY }}
  run: java -jar womba.jar generate -story PLAT-12991
```

## Troubleshooting

### Error: WOMBA_API_URL not set

```bash
export WOMBA_API_URL="https://womba-api.up.railway.app"
```

### Error: WOMBA_API_KEY not set

```bash
export WOMBA_API_KEY="your-api-key"
```

### Error: API error 401

Invalid API key. Check your `WOMBA_API_KEY`.

### Error: Java version

Requires Java 11 or higher. Check version:

```bash
java -version
```

### Error: Unsupported class file major version

Built with Java 11+. Either:
1. Upgrade Java to 11+, or
2. Rebuild with your Java version: `mvn clean package`

## Creating a Bash Wrapper (Optional)

For easier usage, create a wrapper script:

```bash
#!/bin/bash
# Save as 'womba' and place in /usr/local/bin/

java -jar /path/to/womba.jar "$@"
```

Then use:
```bash
womba generate -story PLAT-12991
```

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## License

MIT License - See [LICENSE](LICENSE) file

## Related Projects

- [womba](https://github.com/jtizdev/womba) - Python CLI (core)
- [womba-api](https://github.com/jtizdev/womba-api) - REST API service
- [womba-go](https://github.com/jtizdev/womba-go) - Go CLI
- [womba-node](https://github.com/jtizdev/womba-node) - Node.js CLI
- [womba-forge](https://github.com/jtizdev/womba-forge) - Atlassian Forge plugin

## Support

- **Issues**: https://github.com/jtizdev/womba-java/issues
- **Docs**: https://github.com/jtizdev/womba
- **API Docs**: https://womba-api.up.railway.app/docs

