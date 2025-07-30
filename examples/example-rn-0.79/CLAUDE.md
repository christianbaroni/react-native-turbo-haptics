# Claude: Follow this guide to upgrade RN to 0.79.3. You can replace my AppDelegate file with the new swift version. Just make sure you properly carry over my app-specific values from it.

---

![React Native Upgrade Helper logo](https://react-native-community.github.io/upgrade-helper/static/media/logo.f77432ee46748fb5ada15762c0b86fb6.svg)[**React Native Upgrade Helper**](https://react-native-community.github.io/upgrade-helper)

⚠️⚙️☀️

##### What's your app name?

##### What's your app package?

##### What's your current react-native version?

0.76.1

##### To which version would you like to upgrade?

0.79.3

Show me how to upgrade!

## 📣 Useful content for upgrading

React Native 0.77 changes the AppDelegate template from Obj-C++ to Swift, but it's not only a syntax change. If you stick with the _AppDelegate.mm_ file, be sure to add the new line with _RCTAppDependencyProvider_, as explained in the blog post below.

1. [React Native 0.77 blog post](https://reactnative.dev/blog/2025/01/21/version-0.77#rctappdependencyprovider)
2. [React Native 0.77 changelog](https://github.com/facebook/react-native/blob/main/CHANGELOG.md#v077)

---

You can use the following command to kick off the upgrade: _npx @rnx-kit/align-deps --requirements react-native@\[major.minor\]_.

[_align-deps_](https://microsoft.github.io/rnx-kit/docs/tools/align-deps) is an OSS tool from Microsoft that automates dependency management. It knows which packages\* versions are compatible with your specific version of RN, and it uses that knowledge to align dependencies, keeping your app healthy and up-to-date\*\*. [Find out more here](https://microsoft.github.io/rnx-kit/docs/guides/dependency-management).

\\\* Not all packages are supported out-of-the-box.

\\_\\_ You still need to do the other changes below and verify the changelogs of the libraries that got upgraded.

---

Check out [Upgrade Support](https://github.com/react-native-community/upgrade-support) if you are experiencing issues related to React Native during the upgrading process.

[changelog](https://github.com/facebook/react-native/blob/main/CHANGELOG.md#v0793)

Binaries

SplitUnified

package.jsonMODIFIED

0 hidden comment

[Raw](https://raw.githubusercontent.com/react-native-community/rn-diff-purge/release/0.79.3/RnDiffApp/package.json)

|                     |                                                           |     |                                                           |
| ------------------- | --------------------------------------------------------- | --- | --------------------------------------------------------- |
| @@ -10,31 +10,30 @@ |
| 10                  | "test": "jest"                                            | 10  | "test": "jest"                                            |
| 11                  | },                                                        | 11  | },                                                        |
| 12                  | "dependencies": {                                         | 12  | "dependencies": {                                         |
| 13                  | "react": "18.3.1",                                        |     |                                                           |
| 14                  | "react-native": "0.76.1"                                  | 13  | "react": "19.0.0",                                        |
|                     |                                                           | 14  | "react-native": "0.79.3"                                  |
| 15                  | },                                                        | 15  | },                                                        |
| 16                  | "devDependencies": {                                      | 16  | "devDependencies": {                                      |
| 17                  | "@babel/core": "^7.25.2",                                 | 17  | "@babel/core": "^7.25.2",                                 |
| 18                  | "@babel/preset-env": "^7.25.3",                           | 18  | "@babel/preset-env": "^7.25.3",                           |
| 19                  | "@babel/runtime": "^7.25.0",                              | 19  | "@babel/runtime": "^7.25.0",                              |
| 20                  | "@react-native-community/cli": "15.0.0",                  |     |                                                           |
| 21                  | "@react-native-community/cli-platform-android": "15.0.0", |     |                                                           |
| 22                  | "@react-native-community/cli-platform-ios": "15.0.0",     |     |                                                           |
| 23                  | "@react-native/babel-preset": "0.76.1",                   |     |                                                           |
| 24                  | "@react-native/eslint-config": "0.76.1",                  |     |                                                           |
| 25                  | "@react-native/metro-config": "0.76.1",                   |     |                                                           |
| 26                  | "@react-native/typescript-config": "0.76.1",              |     |                                                           |
| 27                  | "@types/react": "^18.2.6",                                |     |                                                           |
| 28                  | "@types/react-test-renderer": "^18.0.0",                  |     |                                                           |
| 29                  | "babel-jest": "^29.6.3",                                  | 20  | "@react-native-community/cli": "18.0.0",                  |
|                     |                                                           | 21  | "@react-native-community/cli-platform-android": "18.0.0", |
|                     |                                                           | 22  | "@react-native-community/cli-platform-ios": "18.0.0",     |
|                     |                                                           | 23  | "@react-native/babel-preset": "0.79.3",                   |
|                     |                                                           | 24  | "@react-native/eslint-config": "0.79.3",                  |
|                     |                                                           | 25  | "@react-native/metro-config": "0.79.3",                   |
|                     |                                                           | 26  | "@react-native/typescript-config": "0.79.3",              |
|                     |                                                           | 27  | "@types/jest": "^29.5.13",                                |
|                     |                                                           | 28  | "@types/react": "^19.0.0",                                |
|                     |                                                           | 29  | "@types/react-test-renderer": "^19.0.0",                  |
| 30                  | "eslint": "^8.19.0",                                      | 30  | "eslint": "^8.19.0",                                      |
| 31                  | "jest": "^29.6.3",                                        | 31  | "jest": "^29.6.3",                                        |
| 32                  | "prettier": "2.8.8",                                      | 32  | "prettier": "2.8.8",                                      |
| 33                  | "react-test-renderer": "18.3.1",                          | 33  | "react-test-renderer": "19.0.0",                          |
| 34                  | "typescript": "5.0.4"                                     | 34  | "typescript": "5.0.4"                                     |
| 35                  | },                                                        | 35  | },                                                        |
| 36                  | "engines": {                                              | 36  | "engines": {                                              |
| 37                  | "node": ">=18"                                            | 37  | "node": ">=18"                                            |
| 38                  | },                                                        |     |                                                           |
| 39                  | "packageManager": "yarn@3.6.4"                            | 38  | }                                                         |
| 40                  | }                                                         | 39  | }                                                         |

.gitignoreMODIFIED

0 hidden comment

[Raw](https://raw.githubusercontent.com/react-native-community/rn-diff-purge/release/0.79.3/RnDiffApp/.gitignore)

|                                    |                 |     |                 |
| ---------------------------------- | --------------- | --- | --------------- |
| @@ -33,6 +33,7 @@ local.properties |
| 33                                 | .cxx/           | 33  | .cxx/           |
| 34                                 | \*.keystore     | 34  | \*.keystore     |
| 35                                 | !debug.keystore | 35  | !debug.keystore |
|                                    |                 | 36  | .kotlin/        |
| 36                                 |                 | 37  |                 |
| 37                                 | \# node.js      | 38  | \# node.js      |
| 38                                 | #               | 39  | #               |

.yarnrc.ymlDELETED

0 hidden comment

App.tsxMODIFIED

0 hidden comment

[Raw](https://raw.githubusercontent.com/react-native-community/rn-diff-purge/release/0.79.3/RnDiffApp/App.tsx)

|                                                         |                                                                   |     |                                                                                          |
| ------------------------------------------------------- | ----------------------------------------------------------------- | --- | ---------------------------------------------------------------------------------------- |
| @@ -8,7 +8,6 @@                                         |
| 8                                                       | import React from 'react';                                        | 8   | import React from 'react';                                                               |
| 9                                                       | import type {PropsWithChildren} from 'react';                     | 9   | import type {PropsWithChildren} from 'react';                                            |
| 10                                                      | import {                                                          | 10  | import {                                                                                 |
| 11                                                      | SafeAreaView,                                                     |     |                                                                                          |
| 12                                                      | ScrollView,                                                       | 11  | ScrollView,                                                                              |
| 13                                                      | StatusBar,                                                        | 12  | StatusBar,                                                                               |
| 14                                                      | StyleSheet,                                                       | 13  | StyleSheet,                                                                              |
| @@ -62,19 +61,33 @@ function App(): React.JSX.Element { |
| 62                                                      | backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,     | 61  | backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,                            |
| 63                                                      | };                                                                | 62  | };                                                                                       |
| 64                                                      |                                                                   | 63  |                                                                                          |
|                                                         |                                                                   | 64  | /\*                                                                                      |
|                                                         |                                                                   | 65  | \\\* To keep the template simple and small we're adding padding to prevent view          |
|                                                         |                                                                   | 66  | \\\* from rendering under the System UI.                                                 |
|                                                         |                                                                   | 67  | \\\* For bigger apps the recommendation is to use \`react-native-safe-area-context\`:    |
|                                                         |                                                                   | 68  | \\\* https://github.com/AppAndFlow/react-native-safe-area-context                        |
|                                                         |                                                                   | 69  | \*                                                                                       |
|                                                         |                                                                   | 70  | \\\* You can read more about it here:                                                    |
|                                                         |                                                                   | 71  | \\\* https://github.com/react-native-community/discussions-and-proposals/discussions/827 |
|                                                         |                                                                   | 72  | \*/                                                                                      |
|                                                         |                                                                   | 73  | const safePadding = '5%';                                                                |
|                                                         |                                                                   | 74  |                                                                                          |
| 65                                                      | return (                                                          | 75  | return (                                                                                 |
| 66                                                      | <SafeAreaView style={backgroundStyle}>                            | 76  | <View style={backgroundStyle}>                                                           |
| 67                                                      | <StatusBar                                                        | 77  | <StatusBar                                                                               |
| 68                                                      | barStyle={isDarkMode ? 'light-content' : 'dark-content'}          | 78  | barStyle={isDarkMode ? 'light-content' : 'dark-content'}                                 |
| 69                                                      | backgroundColor={backgroundStyle.backgroundColor}                 | 79  | backgroundColor={backgroundStyle.backgroundColor}                                        |
| 70                                                      | />                                                                | 80  | />                                                                                       |
| 71                                                      | <ScrollView                                                       | 81  | <ScrollView                                                                              |
| 72                                                      | contentInsetAdjustmentBehavior="automatic"                        |     |                                                                                          |
| 73                                                      | style={backgroundStyle}>                                          | 82  | style={backgroundStyle}>                                                                 |
|                                                         |                                                                   | 83  | <View style={{paddingRight: safePadding}}>                                               |
| 74                                                      | <Header/>                                                         | 84  | <Header/>                                                                                |
|                                                         |                                                                   | 85  | </View>                                                                                  |
| 75                                                      | <View                                                             | 86  | <View                                                                                    |
| 76                                                      | style={{                                                          | 87  | style={{                                                                                 |
| 77                                                      | backgroundColor: isDarkMode ? Colors.black : Colors.white,        | 88  | backgroundColor: isDarkMode ? Colors.black : Colors.white,                               |
|                                                         |                                                                   | 89  | paddingHorizontal: safePadding,                                                          |
|                                                         |                                                                   | 90  | paddingBottom: safePadding,                                                              |
| 78                                                      | }}>                                                               | 91  | }}>                                                                                      |
| 79                                                      | <Section title="Step One">                                        | 92  | <Section title="Step One">                                                               |
| 80                                                      | Edit <Text style={styles.highlight}>App.tsx</Text> to change this | 93  | Edit <Text style={styles.highlight}>App.tsx</Text> to change this                        |
| @@ -92,7 +105,7 @@ function App(): React.JSX.Element {  |
| 92                                                      | <LearnMoreLinks />                                                | 105 | <LearnMoreLinks />                                                                       |
| 93                                                      | </View>                                                           | 106 | </View>                                                                                  |
| 94                                                      | </ScrollView>                                                     | 107 | </ScrollView>                                                                            |
| 95                                                      | </SafeAreaView>                                                   | 108 | </View>                                                                                  |
| 96                                                      | );                                                                | 109 | );                                                                                       |
| 97                                                      | }                                                                 | 110 | }                                                                                        |
| 98                                                      |                                                                   | 111 |                                                                                          |

GemfileMODIFIED

0 hidden comment

[Raw](https://raw.githubusercontent.com/react-native-community/rn-diff-purge/release/0.79.3/RnDiffApp/Gemfile)

|                                   |                                                      |     |                                                                     |
| --------------------------------- | ---------------------------------------------------- | --- | ------------------------------------------------------------------- |
| @@ -7,3 +7,10 @@ ruby ">= 2.6.10" |
| 7                                 | gem 'cocoapods', '>= 1.13', '!= 1.15.0', '!= 1.15.1' | 7   | gem 'cocoapods', '>= 1.13', '!= 1.15.0', '!= 1.15.1'                |
| 8                                 | gem 'activesupport', '>= 6.1.7.5', '!= 7.1.0'        | 8   | gem 'activesupport', '>= 6.1.7.5', '!= 7.1.0'                       |
| 9                                 | gem 'xcodeproj', '< 1.26.0'                          | 9   | gem 'xcodeproj', '< 1.26.0'                                         |
|                                   |                                                      | 10  | gem 'concurrent-ruby', '< 1.3.4'                                    |
|                                   |                                                      | 11  |                                                                     |
|                                   |                                                      | 12  | \# Ruby 3.4.0 has removed some libraries from the standard library. |
|                                   |                                                      | 13  | gem 'bigdecimal'                                                    |
|                                   |                                                      | 14  | gem 'logger'                                                        |
|                                   |                                                      | 15  | gem 'benchmark'                                                     |
|                                   |                                                      | 16  | gem 'mutex_m'                                                       |

README.mdMODIFIED

0 hidden comment

[Raw](https://raw.githubusercontent.com/react-native-community/rn-diff-purge/release/0.79.3/RnDiffApp/README.md)

|                                                                                                         |                                                                                                                                                                                                                                                |     |                                                                                                                                                                                                                                     |
| ------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| @@ -2,58 +2,76 @@ This is a new \[\*\*React Native\*\*\](https://reactnative.dev) project, bootstrapped |
| 2                                                                                                       |                                                                                                                                                                                                                                                | 2   |                                                                                                                                                                                                                                     |
| 3                                                                                                       | \# Getting Started                                                                                                                                                                                                                             | 3   | \# Getting Started                                                                                                                                                                                                                  |
| 4                                                                                                       |                                                                                                                                                                                                                                                | 4   |                                                                                                                                                                                                                                     |
| 5                                                                                                       | >\*\*Note\*\*: Make sure you have completed the \[React Native - Environment Setup\](https://reactnative.dev/docs/environment-setup) instructions till "Creating a new application" step, before proceeding.                                   | 5   | >\*\*Note\*\*: Make sure you have completed the \[Set Up Your Environment\](https://reactnative.dev/docs/set-up-your-environment) guide before proceeding.                                                                          |
| 6                                                                                                       |                                                                                                                                                                                                                                                | 6   |                                                                                                                                                                                                                                     |
| 7                                                                                                       | \## Step 1: Start the Metro Server                                                                                                                                                                                                             | 7   | \## Step 1: Start Metro                                                                                                                                                                                                             |
| 8                                                                                                       |                                                                                                                                                                                                                                                | 8   |                                                                                                                                                                                                                                     |
| 9                                                                                                       | First, you will need to start \*\*Metro\*\*, the JavaScript \_bundler\_ that ships \_with\_ React Native.                                                                                                                                      | 9   | First, you will need to run \*\*Metro\*\*, the JavaScript build tool for React Native.                                                                                                                                              |
| 10                                                                                                      |                                                                                                                                                                                                                                                | 10  |                                                                                                                                                                                                                                     |
| 11                                                                                                      | To start Metro, run the following command from the \_root\_ of your React Native project:                                                                                                                                                      | 11  | To start the Metro dev server, run the following command from the root of your React Native project:                                                                                                                                |
| 12                                                                                                      |                                                                                                                                                                                                                                                | 12  |                                                                                                                                                                                                                                     |
| 13                                                                                                      | \`\`\`bash                                                                                                                                                                                                                                     |     |                                                                                                                                                                                                                                     |
| 14                                                                                                      | \# using npm                                                                                                                                                                                                                                   | 13  | \`\`\`sh                                                                                                                                                                                                                            |
|                                                                                                         |                                                                                                                                                                                                                                                | 14  | \# Using npm                                                                                                                                                                                                                        |
| 15                                                                                                      | npm start                                                                                                                                                                                                                                      | 15  | npm start                                                                                                                                                                                                                           |
| 16                                                                                                      |                                                                                                                                                                                                                                                | 16  |                                                                                                                                                                                                                                     |
| 17                                                                                                      | \# OR using Yarn                                                                                                                                                                                                                               | 17  | \# OR using Yarn                                                                                                                                                                                                                    |
| 18                                                                                                      | yarn start                                                                                                                                                                                                                                     | 18  | yarn start                                                                                                                                                                                                                          |
| 19                                                                                                      | \`\`\`                                                                                                                                                                                                                                         | 19  | \`\`\`                                                                                                                                                                                                                              |
| 20                                                                                                      |                                                                                                                                                                                                                                                | 20  |                                                                                                                                                                                                                                     |
| 21                                                                                                      | \## Step 2: Start your Application                                                                                                                                                                                                             | 21  | \## Step 2: Build and run your app                                                                                                                                                                                                  |
| 22                                                                                                      |                                                                                                                                                                                                                                                | 22  |                                                                                                                                                                                                                                     |
| 23                                                                                                      | Let Metro Bundler run in its \_own\_ terminal. Open a \_new\_ terminal from the \_root\_ of your React Native project. Run the following command to start your \_Android\_ or \_iOS\_ app:                                                     | 23  | With Metro running, open a new terminal window/pane from the root of your React Native project, and use one of the following commands to build and run your Android or iOS app:                                                     |
| 24                                                                                                      |                                                                                                                                                                                                                                                | 24  |                                                                                                                                                                                                                                     |
| 25                                                                                                      | ### For Android                                                                                                                                                                                                                                | 25  | \### Android                                                                                                                                                                                                                        |
| 26                                                                                                      |                                                                                                                                                                                                                                                | 26  |                                                                                                                                                                                                                                     |
| 27                                                                                                      | \`\`\`bash                                                                                                                                                                                                                                     |     |                                                                                                                                                                                                                                     |
| 28                                                                                                      | \# using npm                                                                                                                                                                                                                                   | 27  | \`\`\`sh                                                                                                                                                                                                                            |
|                                                                                                         |                                                                                                                                                                                                                                                | 28  | \# Using npm                                                                                                                                                                                                                        |
| 29                                                                                                      | npm run android                                                                                                                                                                                                                                | 29  | npm run android                                                                                                                                                                                                                     |
| 30                                                                                                      |                                                                                                                                                                                                                                                | 30  |                                                                                                                                                                                                                                     |
| 31                                                                                                      | \# OR using Yarn                                                                                                                                                                                                                               | 31  | \# OR using Yarn                                                                                                                                                                                                                    |
| 32                                                                                                      | yarn android                                                                                                                                                                                                                                   | 32  | yarn android                                                                                                                                                                                                                        |
| 33                                                                                                      | \`\`\`                                                                                                                                                                                                                                         | 33  | \`\`\`                                                                                                                                                                                                                              |
| 34                                                                                                      |                                                                                                                                                                                                                                                | 34  |                                                                                                                                                                                                                                     |
| 35                                                                                                      | ### For iOS                                                                                                                                                                                                                                    | 35  | \### iOS                                                                                                                                                                                                                            |
| 36                                                                                                      |                                                                                                                                                                                                                                                | 36  |                                                                                                                                                                                                                                     |
| 37                                                                                                      | \`\`\`bash                                                                                                                                                                                                                                     |     |                                                                                                                                                                                                                                     |
| 38                                                                                                      | \# using npm                                                                                                                                                                                                                                   | 37  | For iOS, remember to install CocoaPods dependencies (this only needs to be run on first clone or after updating native deps).                                                                                                       |
|                                                                                                         |                                                                                                                                                                                                                                                | 38  |                                                                                                                                                                                                                                     |
|                                                                                                         |                                                                                                                                                                                                                                                | 39  | The first time you create a new project, run the Ruby bundler to install CocoaPods itself:                                                                                                                                          |
|                                                                                                         |                                                                                                                                                                                                                                                | 40  |                                                                                                                                                                                                                                     |
|                                                                                                         |                                                                                                                                                                                                                                                | 41  | \`\`\`sh                                                                                                                                                                                                                            |
|                                                                                                         |                                                                                                                                                                                                                                                | 42  | bundle install                                                                                                                                                                                                                      |
|                                                                                                         |                                                                                                                                                                                                                                                | 43  | \`\`\`                                                                                                                                                                                                                              |
|                                                                                                         |                                                                                                                                                                                                                                                | 44  |                                                                                                                                                                                                                                     |
|                                                                                                         |                                                                                                                                                                                                                                                | 45  | Then, and every time you update your native dependencies, run:                                                                                                                                                                      |
|                                                                                                         |                                                                                                                                                                                                                                                | 46  |                                                                                                                                                                                                                                     |
|                                                                                                         |                                                                                                                                                                                                                                                | 47  | \`\`\`sh                                                                                                                                                                                                                            |
|                                                                                                         |                                                                                                                                                                                                                                                | 48  | bundle exec pod install                                                                                                                                                                                                             |
|                                                                                                         |                                                                                                                                                                                                                                                | 49  | \`\`\`                                                                                                                                                                                                                              |
|                                                                                                         |                                                                                                                                                                                                                                                | 50  |                                                                                                                                                                                                                                     |
|                                                                                                         |                                                                                                                                                                                                                                                | 51  | For more information, please visit \[CocoaPods Getting Started guide\](https://guides.cocoapods.org/using/getting-started.html).                                                                                                    |
|                                                                                                         |                                                                                                                                                                                                                                                | 52  |                                                                                                                                                                                                                                     |
|                                                                                                         |                                                                                                                                                                                                                                                | 53  | \`\`\`sh                                                                                                                                                                                                                            |
|                                                                                                         |                                                                                                                                                                                                                                                | 54  | \# Using npm                                                                                                                                                                                                                        |
| 39                                                                                                      | npm run ios                                                                                                                                                                                                                                    | 55  | npm run ios                                                                                                                                                                                                                         |
| 40                                                                                                      |                                                                                                                                                                                                                                                | 56  |                                                                                                                                                                                                                                     |
| 41                                                                                                      | \# OR using Yarn                                                                                                                                                                                                                               | 57  | \# OR using Yarn                                                                                                                                                                                                                    |
| 42                                                                                                      | yarn ios                                                                                                                                                                                                                                       | 58  | yarn ios                                                                                                                                                                                                                            |
| 43                                                                                                      | \`\`\`                                                                                                                                                                                                                                         | 59  | \`\`\`                                                                                                                                                                                                                              |
| 44                                                                                                      |                                                                                                                                                                                                                                                | 60  |                                                                                                                                                                                                                                     |
| 45                                                                                                      | If everything is set up \_correctly\_, you should see your new app running in your \_Android Emulator\_ or \_iOS Simulator\_ shortly provided you have set up your emulator/simulator correctly.                                               | 61  | If everything is set up correctly, you should see your new app running in the Android Emulator, iOS Simulator, or your connected device.                                                                                            |
|                                                                                                         |                                                                                                                                                                                                                                                | 62  |                                                                                                                                                                                                                                     |
|                                                                                                         |                                                                                                                                                                                                                                                | 63  | This is one way to run your app — you can also build it directly from Android Studio or Xcode.                                                                                                                                      |
| 46                                                                                                      |                                                                                                                                                                                                                                                | 64  |                                                                                                                                                                                                                                     |
| 47                                                                                                      | This is one way to run your app — you can also run it directly from within Android Studio and Xcode respectively.                                                                                                                              | 65  | \## Step 3: Modify your app                                                                                                                                                                                                         |
| 48                                                                                                      |                                                                                                                                                                                                                                                | 66  |                                                                                                                                                                                                                                     |
| 49                                                                                                      | \## Step 3: Modifying your App                                                                                                                                                                                                                 | 67  | Now that you have successfully run the app, let's make changes!                                                                                                                                                                     |
| 50                                                                                                      |                                                                                                                                                                                                                                                | 68  |                                                                                                                                                                                                                                     |
| 51                                                                                                      | Now that you have successfully run the app, let's modify it.                                                                                                                                                                                   | 69  | Open \`App.tsx\` in your text editor of choice and make some changes. When you save, your app will automatically update and reflect these changes — this is powered by \[Fast Refresh\](https://reactnative.dev/docs/fast-refresh). |
| 52                                                                                                      |                                                                                                                                                                                                                                                | 70  |                                                                                                                                                                                                                                     |
| 53                                                                                                      | 1\. Open \`App.tsx\` in your text editor of choice and edit some lines.                                                                                                                                                                        |     |                                                                                                                                                                                                                                     |
| 54                                                                                                      | 2\. For \*\*Android\*\*: Press the <kbd>R</kbd> key twice or select \*\*"Reload"\*\* from the \*\*Developer Menu\*\* (<kbd>Ctrl</kbd> + <kbd>M</kbd> (on Window and Linux) or <kbd>Cmd ⌘</kbd> + <kbd>M</kbd> (on macOS)) to see your changes! | 71  | When you want to forcefully reload, for example to reset the state of your app, you can perform a full reload:                                                                                                                      |
| 55                                                                                                      |                                                                                                                                                                                                                                                | 72  |                                                                                                                                                                                                                                     |
| 56                                                                                                      | For \*\*iOS\*\*: Hit <kbd>Cmd ⌘</kbd> + <kbd>R</kbd> in your iOS Simulator to reload the app and see your changes!                                                                                                                             | 73  | \- \*\*Android\*\*: Press the <kbd>R</kbd> key twice or select \*\*"Reload"\*\* from the \*\*Dev Menu\*\*, accessed via <kbd>Ctrl</kbd> + <kbd>M</kbd> (Windows/Linux) or <kbd>Cmd ⌘</kbd> + <kbd>M</kbd> (macOS).                  |
|                                                                                                         |                                                                                                                                                                                                                                                | 74  | \- \*\*iOS\*\*: Press <kbd>R</kbd> in iOS Simulator.                                                                                                                                                                                |
| 57                                                                                                      |                                                                                                                                                                                                                                                | 75  |                                                                                                                                                                                                                                     |
| 58                                                                                                      | \## Congratulations! :tada:                                                                                                                                                                                                                    | 76  | \## Congratulations! :tada:                                                                                                                                                                                                         |
| 59                                                                                                      |                                                                                                                                                                                                                                                | 77  |                                                                                                                                                                                                                                     |
| @@ -62,11 +80,11 @@ You've successfully run and modified your React Native App. :partying_face:         |
| 62                                                                                                      | \### Now what?                                                                                                                                                                                                                                 | 80  | \### Now what?                                                                                                                                                                                                                      |
| 63                                                                                                      |                                                                                                                                                                                                                                                | 81  |                                                                                                                                                                                                                                     |
| 64                                                                                                      | \- If you want to add this new React Native code to an existing application, check out the \[Integration guide\](https://reactnative.dev/docs/integration-with-existing-apps).                                                                 | 82  | \- If you want to add this new React Native code to an existing application, check out the \[Integration guide\](https://reactnative.dev/docs/integration-with-existing-apps).                                                      |
| 65                                                                                                      | \- If you're curious to learn more about React Native, check out the \[Introduction to React Native\](https://reactnative.dev/docs/getting-started).                                                                                           | 83  | \- If you're curious to learn more about React Native, check out the \[docs\](https://reactnative.dev/docs/getting-started).                                                                                                        |
| 66                                                                                                      |                                                                                                                                                                                                                                                | 84  |                                                                                                                                                                                                                                     |
| 67                                                                                                      | \# Troubleshooting                                                                                                                                                                                                                             | 85  | \# Troubleshooting                                                                                                                                                                                                                  |
| 68                                                                                                      |                                                                                                                                                                                                                                                | 86  |                                                                                                                                                                                                                                     |
| 69                                                                                                      | If you can't get this to work, see the \[Troubleshooting\](https://reactnative.dev/docs/troubleshooting) page.                                                                                                                                 | 87  | If you're having issues getting the above steps to work, see the \[Troubleshooting\](https://reactnative.dev/docs/troubleshooting) page.                                                                                            |
| 70                                                                                                      |                                                                                                                                                                                                                                                | 88  |                                                                                                                                                                                                                                     |
| 71                                                                                                      | \# Learn More                                                                                                                                                                                                                                  | 89  | \# Learn More                                                                                                                                                                                                                       |
| 72                                                                                                      |                                                                                                                                                                                                                                                | 90  |                                                                                                                                                                                                                                     |

\_\_tests\_\_/App.test.tsxMODIFIED

0 hidden comment

[Raw](https://raw.githubusercontent.com/react-native-community/rn-diff-purge/release/0.79.3/RnDiffApp/__tests__/App.test.tsx)

|                   |                                                                |     |                                                      |
| ----------------- | -------------------------------------------------------------- | --- | ---------------------------------------------------- |
| @@ -2,16 +2,12 @@ |
| 2                 | \\\* @format                                                   | 2   | \\\* @format                                         |
| 3                 | \*/                                                            | 3   | \*/                                                  |
| 4                 |                                                                | 4   |                                                      |
| 5                 | import 'react-native';                                         |     |                                                      |
| 6                 | import React from 'react';                                     | 5   | import React from 'react';                           |
|                   |                                                                | 6   | import ReactTestRenderer from 'react-test-renderer'; |
| 7                 | import App from '../App';                                      | 7   | import App from '../App';                            |
| 8                 |                                                                | 8   |                                                      |
| 9                 | // Note: import explicitly to use the types shipped with jest. |     |                                                      |
| 10                | import {it} from '@jest/globals';                              |     |                                                      |
| 11                |                                                                |     |                                                      |
| 12                | // Note: test renderer must be required after react-native.    |     |                                                      |
| 13                | import renderer from 'react-test-renderer';                    |     |                                                      |
| 14                |                                                                |     |                                                      |
| 15                | it('renders correctly', () => {                                |     |                                                      |
| 16                | renderer.create(<App />);                                      | 9   | test('renders correctly', async () => {              |
|                   |                                                                | 10  | await ReactTestRenderer.act(() => {                  |
|                   |                                                                | 11  | ReactTestRenderer.create(<App />);                   |
|                   |                                                                | 12  | });                                                  |
| 17                | });                                                            | 13  | });                                                  |

android/app/build.gradleMODIFIED

0 hidden comment

[Raw](https://raw.githubusercontent.com/react-native-community/rn-diff-purge/release/0.79.3/RnDiffApp/android/app/build.gradle)

|                                                               |                                                                                     |     |                                                                                      |
| ------------------------------------------------------------- | ----------------------------------------------------------------------------------- | --- | ------------------------------------------------------------------------------------ |
| @@ -63,14 +63,14 @@ def enableProguardInReleaseBuilds = false |
| 63                                                            | \\\* The preferred build flavor of JavaScriptCore (JSC)                             | 63  | \\\* The preferred build flavor of JavaScriptCore (JSC)                              |
| 64                                                            | \*                                                                                  | 64  | \*                                                                                   |
| 65                                                            | \\\* For example, to use the international variant, you can use:                    | 65  | \\\* For example, to use the international variant, you can use:                     |
| 66                                                            | \\\* \`def jscFlavor = 'org.webkit:android-jsc-intl:+'\`                            | 66  | \\\* \`def jscFlavor = io.github.react-native-community:jsc-android-intl:2026004.+\` |
| 67                                                            | \*                                                                                  | 67  | \*                                                                                   |
| 68                                                            | \\\* The international variant includes ICU i18n library and necessary data         | 68  | \\\* The international variant includes ICU i18n library and necessary data          |
| 69                                                            | \\\* allowing to use e.g. \`Date.toLocaleString\` and \`String.localeCompare\` that | 69  | \\\* allowing to use e.g. \`Date.toLocaleString\` and \`String.localeCompare\` that  |
| 70                                                            | \\\* give correct results when using with locales other than en-US. Note that       | 70  | \\\* give correct results when using with locales other than en-US. Note that        |
| 71                                                            | \\\* this variant is about 6MiB larger per architecture than default.               | 71  | \\\* this variant is about 6MiB larger per architecture than default.                |
| 72                                                            | \*/                                                                                 | 72  | \*/                                                                                  |
| 73                                                            | def jscFlavor = 'org.webkit:android-jsc:+'                                          | 73  | def jscFlavor = 'io.github.react-native-community:jsc-android:2026004.+'             |
| 74                                                            |                                                                                     | 74  |                                                                                      |
| 75                                                            | android {                                                                           | 75  | android {                                                                            |
| 76                                                            | ndkVersion rootProject.ext.ndkVersion                                               | 76  | ndkVersion rootProject.ext.ndkVersion                                                |

android/build.gradleMODIFIED

0 hidden comment

[Raw](https://raw.githubusercontent.com/react-native-community/rn-diff-purge/release/0.79.3/RnDiffApp/android/build.gradle)

|                               |                              |     |                              |
| ----------------------------- | ---------------------------- | --- | ---------------------------- |
| @@ -3,9 +3,9 @@ buildscript { |
| 3                             | buildToolsVersion = "35.0.0" | 3   | buildToolsVersion = "35.0.0" |
| 4                             | minSdkVersion = 24           | 4   | minSdkVersion = 24           |
| 5                             | compileSdkVersion = 35       | 5   | compileSdkVersion = 35       |
| 6                             | targetSdkVersion = 34        |     |                              |
| 7                             | ndkVersion = "26.1.10909125" |     |                              |
| 8                             | kotlinVersion = "1.9.24"     | 6   | targetSdkVersion = 35        |
|                               |                              | 7   | ndkVersion = "27.1.12297006" |
|                               |                              | 8   | kotlinVersion = "2.0.21"     |
| 9                             | }                            | 9   | }                            |
| 10                            | repositories {               | 10  | repositories {               |
| 11                            | google()                     | 11  | google()                     |

android/gradle/wrapper/gradle-wrapper.jarMODIFIEDBINARY

0 hidden comment

|
|

android/gradle/wrapper/gradle-wrapper.propertiesMODIFIED

0 hidden comment

[Raw](https://raw.githubusercontent.com/react-native-community/rn-diff-purge/release/0.79.3/RnDiffApp/android/gradle/wrapper/gradle-wrapper.properties)

|                 |                                                                                   |     |                                                                                 |
| --------------- | --------------------------------------------------------------------------------- | --- | ------------------------------------------------------------------------------- |
| @@ -1,6 +1,6 @@ |
| 1               | distributionBase=GRADLE_USER_HOME                                                 | 1   | distributionBase=GRADLE_USER_HOME                                               |
| 2               | distributionPath=wrapper/dists                                                    | 2   | distributionPath=wrapper/dists                                                  |
| 3               | distributionUrl=https\\://services.gradle.org/distributions/gradle-8.10.2-all.zip | 3   | distributionUrl=https\\://services.gradle.org/distributions/gradle-8.13-bin.zip |
| 4               | networkTimeout=10000                                                              | 4   | networkTimeout=10000                                                            |
| 5               | validateDistributionUrl=true                                                      | 5   | validateDistributionUrl=true                                                    |
| 6               | zipStoreBase=GRADLE_USER_HOME                                                     | 6   | zipStoreBase=GRADLE_USER_HOME                                                   |

android/gradlewMODIFIED

0 hidden comment

[Raw](https://raw.githubusercontent.com/react-native-community/rn-diff-purge/release/0.79.3/RnDiffApp/android/gradlew)

|                        |                                                                                                                  |     |                                                                                                                  |
| ---------------------- | ---------------------------------------------------------------------------------------------------------------- | --- | ---------------------------------------------------------------------------------------------------------------- |
| @@ -86,8 +86,7 @@ done |
| 86                     | \# shellcheck disable=SC2034                                                                                     | 86  | \# shellcheck disable=SC2034                                                                                     |
| 87                     | APP_BASE_NAME=${0##\*/}                                                                                          | 87  | APP_BASE_NAME=${0##\*/}                                                                                          |
| 88                     | \# Discard cd standard output in case $CDPATH is set (https://github.com/gradle/gradle/issues/25036)             | 88  | \# Discard cd standard output in case $CDPATH is set (https://github.com/gradle/gradle/issues/25036)             |
| 89                     | APP_HOME=$( cd -P "${APP_HOME:-./}" > /dev/null && printf '%s                                                    |     |                                                                                                                  |
| 90                     | ' "$PWD" ) \|\| exit                                                                                             | 89  | APP_HOME=$( cd -P "${APP_HOME:-./}" > /dev/null && printf '%s\\n' "$PWD" ) \|\| exit                             |
| 91                     |                                                                                                                  | 90  |                                                                                                                  |
| 92                     | \# Use the maximum available, or set MAX_FD != -1 to use that value.                                             | 91  | \# Use the maximum available, or set MAX_FD != -1 to use that value.                                             |
| 93                     | MAX_FD=maximum                                                                                                   | 92  | MAX_FD=maximum                                                                                                   |
| @@ -206,7 +205,7 @@ fi |
| 206                    | DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'                                                                           | 205 | DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'                                                                           |
| 207                    |                                                                                                                  | 206 |                                                                                                                  |
| 208                    | \# Collect all arguments for the java command:                                                                   | 207 | \# Collect all arguments for the java command:                                                                   |
| 209                    | \# \* DEFAULT_JVM_OPTS, JAVA_OPTS, JAVA_OPTS, and optsEnvironmentVar are not allowed to contain shell fragments, | 208 | \# \* DEFAULT_JVM_OPTS, JAVA_OPTS, and optsEnvironmentVar are not allowed to contain shell fragments,            |
| 210                    | \# and any embedded shellness will be escaped.                                                                   | 209 | \# and any embedded shellness will be escaped.                                                                   |
| 211                    | \# \* For example: A user cannot expect ${Hostname} to be expanded, as it is an environment variable and will be | 210 | \# \* For example: A user cannot expect ${Hostname} to be expanded, as it is an environment variable and will be |
| 212                    | \# treated as '${Hostname}' itself on the command line.                                                          | 211 | \# treated as '${Hostname}' itself on the command line.                                                          |

ios/PodfileMODIFIED

0 hidden comment

[Raw](https://raw.githubusercontent.com/react-native-community/rn-diff-purge/release/0.79.3/RnDiffApp/ios/Podfile)

|                                          |                                                                                                                     |     |                                                                                                                     |
| ---------------------------------------- | ------------------------------------------------------------------------------------------------------------------- | --- | ------------------------------------------------------------------------------------------------------------------- |
| @@ -23,11 +23,6 @@ target 'RnDiffApp' do |
| 23                                       | :app_path => "#{Pod::Config.instance.installation_root}/.."                                                         | 23  | :app_path => "#{Pod::Config.instance.installation_root}/.."                                                         |
| 24                                       | )                                                                                                                   | 24  | )                                                                                                                   |
| 25                                       |                                                                                                                     | 25  |                                                                                                                     |
| 26                                       | target 'RnDiffAppTests' do                                                                                          |     |                                                                                                                     |
| 27                                       | inherit! :complete                                                                                                  |     |                                                                                                                     |
| 28                                       | # Pods for testing                                                                                                  |     |                                                                                                                     |
| 29                                       | end                                                                                                                 |     |                                                                                                                     |
| 30                                       |                                                                                                                     |     |                                                                                                                     |
| 31                                       | post_install do \|installer\|                                                                                       | 26  | post_install do \|installer\|                                                                                       |
| 32                                       | # https://github.com/facebook/react-native/blob/main/packages/react-native/scripts/react\_native\_pods.rb#L197-L202 | 27  | # https://github.com/facebook/react-native/blob/main/packages/react-native/scripts/react\_native\_pods.rb#L197-L202 |
| 33                                       | react_native_post_install(                                                                                          | 28  | react_native_post_install(                                                                                          |

ios/RnDiffApp.xcodeproj/project.pbxprojMODIFIED

0 hidden comment

[Raw](https://raw.githubusercontent.com/react-native-community/rn-diff-purge/release/0.79.3/RnDiffApp/ios/RnDiffApp.xcodeproj/project.pbxproj)

ios/RnDiffApp/AppDelegate.hDELETED

0 hidden comment

ios/RnDiffApp/AppDelegate.mmDELETED

0 hidden comment

ios/RnDiffApp/AppDelegate.swiftADDED

0 hidden comment

[Raw](https://raw.githubusercontent.com/react-native-community/rn-diff-purge/release/0.79.3/RnDiffApp/ios/RnDiffApp/AppDelegate.swift)

|                  |                                                                                             |
| ---------------- | ------------------------------------------------------------------------------------------- |
| @@ -0,0 +1,48 @@ |
| 1                | import UIKit                                                                                |
| 2                | import React                                                                                |
| 3                | import React_RCTAppDelegate                                                                 |
| 4                | import ReactAppDependencyProvider                                                           |
| 5                |                                                                                             |
| 6                | @main                                                                                       |
| 7                | class AppDelegate: UIResponder, UIApplicationDelegate {                                     |
| 8                | var window: UIWindow?                                                                       |
| 9                |                                                                                             |
| 10               | var reactNativeDelegate: ReactNativeDelegate?                                               |
| 11               | var reactNativeFactory: RCTReactNativeFactory?                                              |
| 12               |                                                                                             |
| 13               | func application(                                                                           |
| 14               | \_ application: UIApplication,                                                              |
| 15               | didFinishLaunchingWithOptions launchOptions: \[UIApplication.LaunchOptionsKey: Any\]? = nil |
| 16               | ) -\> Bool {                                                                                |
| 17               | let delegate = ReactNativeDelegate()                                                        |
| 18               | let factory = RCTReactNativeFactory(delegate: delegate)                                     |
| 19               | delegate.dependencyProvider = RCTAppDependencyProvider()                                    |
| 20               |                                                                                             |
| 21               | reactNativeDelegate = delegate                                                              |
| 22               | reactNativeFactory = factory                                                                |
| 23               |                                                                                             |
| 24               | window = UIWindow(frame: UIScreen.main.bounds)                                              |
| 25               |                                                                                             |
| 26               | factory.startReactNative(                                                                   |
| 27               | withModuleName: "RnDiffApp",                                                                |
| 28               | in: window,                                                                                 |
| 29               | launchOptions: launchOptions                                                                |
| 30               | )                                                                                           |
| 31               |                                                                                             |
| 32               | return true                                                                                 |
| 33               | }                                                                                           |
| 34               | }                                                                                           |
| 35               |                                                                                             |
| 36               | class ReactNativeDelegate: RCTDefaultReactNativeFactoryDelegate {                           |
| 37               | override func sourceURL(for bridge: RCTBridge) -> URL? {                                    |
| 38               | self.bundleURL()                                                                            |
| 39               | }                                                                                           |
| 40               |                                                                                             |
| 41               | override func bundleURL() -> URL? {                                                         |
| 42               | #if DEBUG                                                                                   |
| 43               | RCTBundleURLProvider.sharedSettings().jsBundleURL(forBundleRoot: "index")                   |
| 44               | #else                                                                                       |
| 45               | Bundle.main.url(forResource: "main", withExtension: "jsbundle")                             |
| 46               | #endif                                                                                      |
| 47               | }                                                                                           |
| 48               | }                                                                                           |

ios/RnDiffApp/main.mDELETED

0 hidden comment

ios/RnDiffAppTests/Info.plistDELETED

0 hidden comment

ios/RnDiffAppTests/RnDiffAppTests.mDELETED

0 hidden comment

metro.config.jsMODIFIED

0 hidden comment

[Raw](https://raw.githubusercontent.com/react-native-community/rn-diff-purge/release/0.79.3/RnDiffApp/metro.config.js)

|                                                                                                |                                                 |     |                                                               |
| ---------------------------------------------------------------------------------------------- | ----------------------------------------------- | --- | ------------------------------------------------------------- |
| @@ -4,7 +4,7 @@ const {getDefaultConfig, mergeConfig} = require('@react-native/metro-config'); |
| 4                                                                                              | \\\* Metro configuration                        | 4   | \\\* Metro configuration                                      |
| 5                                                                                              | \\\* https://reactnative.dev/docs/metro         | 5   | \\\* https://reactnative.dev/docs/metro                       |
| 6                                                                                              | \*                                              | 6   | \*                                                            |
| 7                                                                                              | \\\* @type {import('metro-config').MetroConfig} | 7   | \\\* @type {import('@react-native/metro-config').MetroConfig} |
| 8                                                                                              | \*/                                             | 8   | \*/                                                           |
| 9                                                                                              | const config = {};                              | 9   | const config = {};                                            |
| 10                                                                                             |                                                 | 10  |                                                               |

1 /21
