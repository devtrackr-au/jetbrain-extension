# Publishing DevTrackr JetBrains Plugin

This guide will help you publish the DevTrackr plugin to the JetBrains Plugin Repository.

## Prerequisites

1. **JetBrains Account** - Required for publishing
2. **Plugin ZIP file** - Built using `./gradlew buildPlugin`
3. **Personal Access Token** - Generated from JetBrains Account (for automated publishing)

## Step-by-Step Publishing Guide

### 1. Create a JetBrains Account

If you don't have one:
1. Go to https://account.jetbrains.com
2. Sign up for a free account
3. Verify your email address

### 2. First-Time Publication (Manual Upload)

**Important:** The first plugin publication must be uploaded manually through the web interface.

#### Step 2.1: Build the Plugin

```bash
cd devtrackr-jetbrains
./gradlew clean buildPlugin
```

This creates: `build/distributions/devtrackr-jetbrains-1.0.0.zip`

#### Step 2.2: Upload to JetBrains Marketplace

1. Go to [JetBrains Marketplace - My Plugins](https://plugins.jetbrains.com/author/me)
2. Click **"Add new plugin"** button
3. Fill out the plugin information:
   - **Plugin name:** DevTrackr - Time Tracking for Developers
   - **Plugin ID:** com.devtrackr.jetbrains (must match plugin.xml)
   - **Version:** 1.0.0
   - **Description:** Copy from plugin.xml or README.md
   - **Category:** Productivity Tools or Developer Tools
   - **Tags:** time-tracking, productivity, analytics, developer-tools
   - **Repository URL:** (optional) Your GitHub repository URL
   - **VCS URL:** (optional) Your version control URL
4. Upload the ZIP file: `build/distributions/devtrackr-jetbrains-1.0.0.zip`
5. Add screenshots (optional but recommended)
6. Review and submit for approval

#### Step 2.3: Wait for Review

- First-time publications typically take **24-48 hours** for review
- You'll receive an email notification when approved or if changes are needed

### 3. Automated Publishing (After First Publication)

Once your plugin is published at least once, you can automate future releases using Gradle.

#### Step 3.1: Generate Personal Access Token

1. Go to [JetBrains Account - Tokens](https://plugins.jetbrains.com/author/me/tokens)
2. Click **"Generate token"**
3. Give it a name: `DevTrackr Plugin Publishing`
4. Set expiration (or leave as default)
5. Click **"Generate"**
6. **Copy the token immediately** (you won't be able to see it again!)

#### Step 3.2: Set Up Publishing Token

**Option A: Environment Variable (Recommended)**

Add to your `~/.zshrc` or `~/.bash_profile`:

```bash
export ORG_GRADLE_PROJECT_intellijPublishToken='YOUR_TOKEN_HERE'
```

Then reload:
```bash
source ~/.zshrc
```

**Option B: Gradle Properties**

Create or edit `~/.gradle/gradle.properties`:

```properties
org.gradle.project.intellijPublishToken=YOUR_TOKEN_HERE
```

**Option C: Command Line (Temporary)**

```bash
./gradlew publishPlugin -Dorg.gradle.project.intellijPublishToken=YOUR_TOKEN_HERE
```

#### Step 3.3: Update Version

Before publishing a new version, update the version in `build.gradle.kts`:

```kotlin
version = "1.0.1" // Increment version number
```

Also update `CHANGELOG.md` with the new version and changes.

#### Step 3.4: Build and Publish

```bash
# Clean and build
./gradlew clean buildPlugin

# Publish to JetBrains Marketplace
./gradlew publishPlugin
```

The plugin will be automatically uploaded and published.

### 4. Plugin Signing (Optional but Recommended)

Plugin signing is optional but recommended for security. To sign your plugin:

#### Step 4.1: Generate Certificate

```bash
# Generate a certificate
keytool -genkey -keyalg RSA -keysize 2048 -validity 10000 \
  -alias devtrackr \
  -keystore devtrackr-keystore.jks \
  -storepass YOUR_STORE_PASSWORD \
  -keypass YOUR_KEY_PASSWORD \
  -dname "CN=DevTrackr, OU=Development, O=DevTrackr, L=City, ST=State, C=US"
```

#### Step 4.2: Configure Signing

Set environment variables:

```bash
export CERTIFICATE_CHAIN="$(cat path/to/certificate-chain.pem)"
export PRIVATE_KEY="$(cat path/to/private-key.pem)"
export PRIVATE_KEY_PASSWORD="YOUR_KEY_PASSWORD"
```

Or update `build.gradle.kts` to read from files.

#### Step 4.3: Sign and Publish

```bash
./gradlew signPlugin publishPlugin
```

## Version Management

### Version Numbering

Follow semantic versioning: `MAJOR.MINOR.PATCH`

- **Major (1.0.0 → 2.0.0):** Breaking changes
- **Minor (1.0.0 → 1.1.0):** New features, backward compatible
- **Patch (1.0.0 → 1.0.1):** Bug fixes, backward compatible

### Update Process

1. Update version in `build.gradle.kts`
2. Update `CHANGELOG.md` with changes
3. Build: `./gradlew clean buildPlugin`
4. Test locally (optional): `./gradlew runIde`
5. Publish: `./gradlew publishPlugin`

## Marketplace Listing

### Required Information

- **Plugin name:** DevTrackr - Time Tracking for Developers
- **Description:** Formatted HTML description (already in plugin.xml)
- **Category:** Productivity Tools
- **Tags:** time-tracking, productivity, analytics, developer-tools, coding-time
- **Icon:** Already included (icon.png)
- **Screenshots:** (Optional but recommended)

### Recommended Screenshots

1. Settings page showing API key configuration
2. Plugin in action (showing tracking status)
3. Dashboard or analytics view (if applicable)

## Quick Reference Commands

```bash
# Build plugin
./gradlew clean buildPlugin

# Test plugin locally
./gradlew runIde

# Publish plugin (after first manual upload)
./gradlew publishPlugin

# Build and publish in one command
./gradlew clean buildPlugin publishPlugin

# Check current version
grep "version = " build.gradle.kts
```

## Troubleshooting

### "Plugin not found" error
- Make sure you've manually uploaded the first version
- Verify the plugin ID matches: `com.devtrackr.jetbrains`

### "Invalid token" error
- Generate a new token from https://plugins.jetbrains.com/author/me/tokens
- Make sure the token hasn't expired
- Verify the token is set correctly in environment variables

### "Version already exists" error
- Increment the version number in `build.gradle.kts`
- Update `CHANGELOG.md`
- Rebuild and republish

### "Plugin verification failed"
- Run: `./gradlew verifyPlugin`
- Fix any compatibility issues
- Ensure all dependencies are properly declared

## Post-Publication

After publishing:

1. **Verify listing:** Visit https://plugins.jetbrains.com/plugin/com.devtrackr.jetbrains
2. **Monitor reviews:** Check for user feedback and ratings
3. **Update documentation:** Keep README.md and CHANGELOG.md updated
4. **Respond to issues:** Address user questions and bug reports

## Important Notes

- **First publication:** Must be done manually through the web interface
- **Subsequent versions:** Can be automated with Gradle
- **Review time:** First publication takes 24-48 hours
- **Update frequency:** Avoid publishing too frequently (wait at least a few minutes between publishes)
- **Testing:** Always test your plugin locally before publishing
- **Versioning:** Always increment version before publishing updates
- **Changelog:** Keep CHANGELOG.md updated with each release

## Resources

- [JetBrains Marketplace](https://plugins.jetbrains.com/)
- [Plugin Publishing Documentation](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html)
- [Marketplace Guidelines](https://plugins.jetbrains.com/docs/marketplace/marketplace-guidelines.html)
- [Plugin Verifier](https://plugins.jetbrains.com/docs/intellij/plugin-verifier.html)
