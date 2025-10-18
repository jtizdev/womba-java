# Womba CLI (Java)

> AI-powered test generation for Jira stories

## Install

```bash
git clone https://github.com/jtizdev/womba-java.git
cd womba-java && mvn clean package
```

## Usage

```bash
# Setup
export WOMBA_API_URL="https://womba-api.onrender.com"
export WOMBA_API_KEY="your-api-key"

# Generate tests
java -jar target/womba.jar generate -story PLAT-12991

# Generate and upload to Zephyr
java -jar target/womba.jar generate -story PLAT-12991 --upload
```

## License

MIT · [Womba](https://github.com/jtizdev/womba)
