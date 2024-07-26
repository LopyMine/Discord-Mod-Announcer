[![Discord link to the "LopyMine's Project" discord server](https://cdn.modrinth.com/data/cached_images/21f178aff2b64844fefeaf94a3a3a418440fd43f.png)](https://discord.gg/NZzxdkrV4s) [![Support Link-Banner [Boosty]](https://cdn.modrinth.com/data/cached_images/dce91fef079649dee277c52a998fc068e745e99e.png)](https://boosty.to/lopymine/donate)

# Discord Mod Announcer

Discord Mod Announcer — Simple Gradle plugin for easily announcing new mods or their updates in your Discord server!

# How To Use

First, you need to add this plugin to your Gradle project:

```gradle
// In build.gradle
plugins {
    id "net.lopymine.discord-mod-announcer" version "1.0.0"
}

// In settings.gradle
pluginManagement {
    repositories {
        // I don't have any repositories or money for it
        // because you need to fork this project and publish to local maven
        mavenLocal()
    }
}
```

Here is a basic example of this plugin configuration:

```gradle
announceToDiscord {
    // If ENABLE, message will be sent to "announcementChannelId"
    // If TEST, message will be sent to "testAnnouncementChannelId" 
    announceMode = TEST // Optional. Can be ENABLE, DISABLE, TEST. Using ENABLE by default
    token = providers.environmentVariable("DISCORD_BOT_TOKEN")
    
    icon = project.rootProject.file("icon.png") // Optional
   
    title = "My Cool Mod v2.0.0 is out!"
    showcaseThreadTitle = "Showcase My Cool Mod v2.0.0" // Optional
    changelog = "- Changelog line one \n - Changelog line two \n - Changelog line three"
    
    modrinthLink = "https://youtu.be/dQw4w9WgXcQ?si=YuNYqbxc3xXANfKl" // Optional
    curseForgeLink = "https://youtu.be/hvL1339luv0?si=m9v6lHiIz7aly3uJ" // Optional
    githubLink = "https://youtu.be/EpX1_YJPGAY?si=MfyB_wTVIv6I3NcZ" // Optional
    
    uploaderId = "616939110598443008" // Optional
    announcementChannelId = "1102941223003631698"
    testAnnouncementChannelId = "1266007822173470730"
    pingRoles = ["Developer", "Mossy"] // Optional

    showcaseImages = [project.rootProject.file("showcase.png"),project.rootProject.file("showcase2.png")] // Optional
}
```

Then you can use task "announceToDiscord" in group "announce" to announce your mod in specific channel
