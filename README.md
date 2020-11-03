<p align="center">
  <img alt="Mc-Auth Logo" width="128px" height="auto" src="https://mc-auth.com/assets/img/Logo.svg">
</p>

<p align="center">
  <a href="https://sprax.me/discord">
    <img alt="Get Support on Discord"
         src="https://img.shields.io/discord/344982818863972352.svg?label=Get%20Support&logo=Discord&color=blue">
  </a>
  <a href="https://www.patreon.com/sprax">
    <img alt="Support me on Patreon"
         src="https://img.shields.io/badge/-Support%20me%20on%20Patreon-%23FF424D?logo=patreon&logoColor=white">
  </a>
</p>

<p align="center">
  <a href="https://github.com/Mc-Auth-com/McAuth-BungeeCord/actions?query=workflow%3A%22Build+with+Maven%22">
    <img alt="Build with Maven" src="https://github.com/Mc-Auth-com/McAuth-BungeeCord/workflows/Build%20with%20Maven/badge.svg">
  </a>

  <a href="https://sonarcloud.io/dashboard?id=Mc-Auth-com_McAuth-BungeeCord">
    <img alt="Quality Gate Status"
         src="https://sonarcloud.io/api/project_badges/measure?project=Mc-Auth-com_McAuth-BungeeCord&metric=alert_status">
  </a>
</p>

# McAuth-BungeeCord
This is a BungeeCord plugin used by [Mc-Auth](https://github.com/Mc-Auth-com/Mc-Auth) to verify Minecraft users
connecting to a BungeeCord instance and generate a 6-digit code that can be provided to *Mc-Auth* to log in.

**Check out the main [Mc-Auth repository](https://github.com/Mc-Auth-com/Mc-Auth) for a more detailed explanation of the project.**

## Setup
**You'll need Java 8 or newer installed on your machine**

**This plugin requires `online-mode` to be set to `true` on BungeeCord to work properly!**

1. Download [BungeeCord](https://ci.md-5.net/job/BungeeCord/)
   (or a comparable fork like [Waterfall](https://papermc.io/downloads#Waterfall))
   and place it inside an empty directory
2. Run BungeeCord using `java -jar BungeeCord.jar` and configure all the created files to your liking
3. Download the [latest release](https://github.com/Mc-Auth-com/McAuth-BungeeCord/releases/latest)
   of this project (or compile it yourself) and place it inside the `./plugins/` subdirectory that has been created by *BungeeCord*
4. Restart *BungeeCord* and configure the generated `./plugins/McAuth-BungeeCord/config.yml`

## License
[MIT License](./LICENSE)