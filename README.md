# Womba Java CLI

AI-powered test generation for Jira stories.

## Installation

```bash
git clone https://github.com/jtizdev/womba-java.git
cd womba-java
mvn clean package
```

## Configuration

```bash
export WOMBA_API_URL="https://womba-api.up.railway.app"
export WOMBA_API_KEY="your-api-key"
```

## Usage

```bash
# Generate tests
java -jar target/womba.jar generate -story PLAT-12991

# Generate and upload to Zephyr
java -jar target/womba.jar generate -story PLAT-12991 --upload

# Check API health
java -jar target/womba.jar health
```

## Support

- [Main Docs](https://github.com/jtizdev/womba)
- [Issues](https://github.com/jtizdev/womba-java/issues)
