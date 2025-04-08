# Winny Labs README

這裡是 Winny 的技術實驗和筆記，隨手記下的有趣想法和各種亂七八糟的記錄。

- [Winny Labs 網站](https://wanyutang.github.io/winny-labs/)
- [開發動作](https://github.com/wanyutang/winny-labs/actions)

`bundle exec jekyll post "My Cool PostXX"`

# Chirpy Starter

[![Gem Version](https://img.shields.io/gem/v/jekyll-theme-chirpy)][gem]&nbsp;
[![GitHub license](https://img.shields.io/github/license/cotes2020/chirpy-starter.svg?color=blue)][mit]

當透過 [RubyGems.org][gem] 安裝 [**Chirpy**][chirpy] 主題時，Jekyll 只能讀取以下資料夾中的檔案：  
`_data`、`_layouts`、`_includes`、`_sass` 和 `assets`，以及主題 gem 的 `_config.yml` 文件中的少部分選項。  
如果你曾安裝過此主題 gem，可以使用以下指令來定位這些檔案：  
`bundle info --path jekyll-theme-chirpy`

Jekyll 團隊聲稱這樣的設計是為了將控制權交給使用者，但這也導致使用者無法享受功能豐富主題的開箱即用體驗。

要充分使用 **Chirpy** 的所有功能，你需要將主題 gem 中其他重要檔案複製到你的 Jekyll 網站中。以下是需要複製的目標列表：

```shell
.
├── _config.yml
├── _plugins
├── _tabs
└── index.html
```

為了節省你的時間，也避免在複製時遺漏文件，我們已將最新版本 **Chirpy** 主題的檔案與 [CD][CD] 工作流程提取到此處，讓你能在幾分鐘內開始撰寫。

## 使用方式

查看 [主題文件](https://github.com/cotes2020/jekyll-theme-chirpy/wiki)。

## 貢獻

此存儲庫會自動更新主題存儲庫的新版本。如果你遇到任何問題或希望改進，請造訪 [主題存儲庫][chirpy] 提供反饋。

## 授權

此作品依 [MIT][mit] 許可證發布。

[gem]: https://rubygems.org/gems/jekyll-theme-chirpy
[chirpy]: https://github.com/cotes2020/jekyll-theme-chirpy/
[CD]: https://en.wikipedia.org/wiki/Continuous_deployment
[mit]: https://github.com/cotes2020/chirpy-starter/blob/master/LICENSE
