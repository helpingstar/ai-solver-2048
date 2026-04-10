# 1. Executive Summary

- Project root: `/Users/helpingstar/dev/2048`
- UI structure summary: single-scene static HTML page with one gameplay surface, one in-board
  overlay container, one dynamic tile layer, one score HUD row, and three static informational
  paragraphs below the board.
- Total routed screens: `1` (`Main Gameplay Screen`; no router, no scene stack, no secondary pages
  in runtime UI).
- Overlay/HUD states: `3` significant runtime overlays/HUD behaviors.
  `Score addition` floating micro-overlay, `Game won` overlay, `Game over` overlay.
- Shared component count: `7` practical Compose component targets.
  `ScoreBadge`, `ActionButton`, `BoardSurface`, `GridCell`, `NumberTile`, `GameMessageOverlay`,
  `ScoreAdditionLabel`.
- Theme/token presence: explicit SCSS variables exist for board size, spacing, base colors,
  transition speed, and tile generation formulas in
  `/Users/helpingstar/dev/2048/style/main.scss:4-22`.
- Migration difficulty: `Low to Moderate`.
  The UI surface is small, but exact parity depends on reproducing CSS timing, 4x4 absolute tile
  translation, pseudo-element score labels, value-specific tile colors/glows, and the delayed
  overlay fade that blocks input before it becomes visible.

# 2. Project UI Inventory

## 2.1 Screens

| Screen               | Description                                                                                                               | Entry Files                                                                                                                                                 | Shared Layout                                                   | Overlay Usage                                                                                   |
|----------------------|---------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------|-------------------------------------------------------------------------------------------------|
| Main Gameplay Screen | Single-page layout containing title, score HUD, intro/restart row, 4x4 board, dynamic tiles, and explanatory/footer text. | `/Users/helpingstar/dev/2048/index.html:20-85`, `/Users/helpingstar/dev/2048/js/application.js:1-4`, `/Users/helpingstar/dev/2048/js/game_manager.js:1-272` | Root `.container` centered at `500px` desktop / `280px` mobile. | `.game-message` overlay inside `.game-container`; `.score-addition` overlay inside score badge. |

## 2.2 Shared Components

| Component          | Variants                                                                                               | Source Files                                                                                                                                                                                       | Reused In                             | Notes                                                                  |
|--------------------|--------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------|------------------------------------------------------------------------|
| ScoreBadge         | `Score`, `Best`                                                                                        | `/Users/helpingstar/dev/2048/index.html:23-25`, `/Users/helpingstar/dev/2048/style/main.scss:67-115`, `/Users/helpingstar/dev/2048/js/html_actuator.js:106-125`                                    | Heading HUD                           | Uses pseudo-element label and optional floating `+N` child overlay.    |
| ActionButton       | `Restart`, `Retry`, `KeepPlaying`                                                                      | `/Users/helpingstar/dev/2048/index.html:31,38-39`, `/Users/helpingstar/dev/2048/style/main.scss:158-168,224-232,464-469`, `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:71-75,130-143` | Above-game row, overlay lower actions | Same visual token; different text and visibility rules.                |
| BoardSurface       | Single visual variant, desktop/mobile sizes                                                            | `/Users/helpingstar/dev/2048/index.html:34-73`, `/Users/helpingstar/dev/2048/style/main.scss:171-195,475-548`                                                                                      | Main gameplay area                    | Fixed-size square with internal padding and local stacking context.    |
| GridCell           | Single visual variant                                                                                  | `/Users/helpingstar/dev/2048/index.html:43-67`, `/Users/helpingstar/dev/2048/style/main.scss:252-284,475-548`                                                                                      | 16 slots in board background layer    | Pure decorative background cells; no direct interaction.               |
| NumberTile         | Value variants `2`..`2048`, `super`, state variants `new`, `merged`, translated positions `1-1`..`4-4` | `/Users/helpingstar/dev/2048/style/main.scss:291-452`, `/Users/helpingstar/dev/2048/style/main.css:231-508`, `/Users/helpingstar/dev/2048/js/html_actuator.js:49-91`                               | Dynamic board layer                   | Wrapper is absolute-positioned; visual surface is child `.tile-inner`. |
| GameMessageOverlay | `game-won`, `game-over`                                                                                | `/Users/helpingstar/dev/2048/index.html:35-40`, `/Users/helpingstar/dev/2048/style/main.scss:196-249,537-548`, `/Users/helpingstar/dev/2048/js/html_actuator.js:127-138`                           | Over board only                       | Hidden by default; blocks board when shown.                            |
| ScoreAdditionLabel | `+difference` only when score delta positive                                                           | `/Users/helpingstar/dev/2048/style/main.scss:95-106`, `/Users/helpingstar/dev/2048/js/html_actuator.js:106-120`                                                                                    | Inside `ScoreBadge`                   | Temporary animated child, not persisted.                               |

## 2.3 Overlays / HUD / Modal / Popup

| Name                                                                                          | Trigger                                | Layout Type                                      | Source Files                                                                                                           | Notes                                                               |
|-----------------------------------------------------------------------------------------------|----------------------------------------|--------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------|
| Score HUD                                                                                     | Always visible                         | Top-right inline HUD row                         | `/Users/helpingstar/dev/2048/index.html:23-25`, `/Users/helpingstar/dev/2048/style/main.scss:62-115`                   | Contains score and best badges.                                     |
| ScoreAdditionLabel                                                                            | `metadata.score - previousScore > 0`   | Absolute child overlay inside `.score-container` | `/Users/helpingstar/dev/2048/js/html_actuator.js:109-120`, `/Users/helpingstar/dev/2048/style/main.scss:95-106`        | Animates upward and fades out over `600ms`.                         |
| GameMessageOverlay / Game Over                                                                | `metadata.terminated && metadata.over` | Full-board absolute overlay                      | `/Users/helpingstar/dev/2048/js/html_actuator.js:27-33,127-133`, `/Users/helpingstar/dev/2048/style/main.scss:196-249` | Message text `Game over!`, only `Try again` button visible.         |
| GameMessageOverlay / Game Won                                                                 | `metadata.terminated && metadata.won`  | Full-board absolute overlay                      | `/Users/helpingstar/dev/2048/js/html_actuator.js:27-33,127-133`, `/Users/helpingstar/dev/2048/style/main.scss:196-249` | Message text `You win!`, `Keep going` and `Try again` both visible. |
| Tooltip / Toast / Snackbar / Dropdown / Context Menu / Bottom Sheet / Loading / Error / Empty | Not implemented                        | None                                             | No source files define these runtime UI patterns.                                                                      | Keep absent in Compose unless product scope expands.                |

# 3. Design Tokens

## 3.1 Colors

| Token/Usage                             | Value                                                                                      | Exact/Derived/Inferred                                      | Source                                                                                                                |
|-----------------------------------------|--------------------------------------------------------------------------------------------|-------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------|
| Page background                         | `#faf8ef`                                                                                  | Exact                                                       | `/Users/helpingstar/dev/2048/style/main.scss:24-32`                                                                   |
| Primary text default                    | `#776e65`                                                                                  | Exact                                                       | `/Users/helpingstar/dev/2048/style/main.scss:12,24-32`                                                                |
| Bright text                             | `#f9f6f2`                                                                                  | Exact                                                       | `/Users/helpingstar/dev/2048/style/main.scss:13`                                                                      |
| Base tile background                    | `#eee4da`                                                                                  | Exact                                                       | `/Users/helpingstar/dev/2048/style/main.scss:15`                                                                      |
| Gold tile anchor color                  | `#edc22e`                                                                                  | Exact                                                       | `/Users/helpingstar/dev/2048/style/main.scss:16`                                                                      |
| Gold glow base color                    | `#f3d774`                                                                                  | Derived from `lighten(#edc22e, 15%)`                        | `/Users/helpingstar/dev/2048/style/main.scss:17`, `/Users/helpingstar/dev/2048/style/main.css:380-384`                |
| Board background                        | `#bbada0`                                                                                  | Exact                                                       | `/Users/helpingstar/dev/2048/style/main.scss:20`                                                                      |
| Button background                       | `#8f7a66`                                                                                  | Derived from `darken(#bbada0, 20%)`                         | `/Users/helpingstar/dev/2048/style/main.scss:159-168`, `/Users/helpingstar/dev/2048/style/main.css:184-193,520-531`   |
| Horizontal rule border                  | `#d8d4d0`                                                                                  | Derived from `lighten(#776e65, 40%)`                        | `/Users/helpingstar/dev/2048/style/main.scss:136-141`, `/Users/helpingstar/dev/2048/style/main.css:112-116`           |
| Grid cell fill                          | `rgba(238, 228, 218, 0.35)`                                                                | Derived from `$tile-color`                                  | `/Users/helpingstar/dev/2048/style/main.scss:271-284`, `/Users/helpingstar/dev/2048/style/main.css:217-225,650-658`   |
| Default overlay fill                    | `rgba(238, 228, 218, 0.5)`                                                                 | Derived from `$tile-color`                                  | `/Users/helpingstar/dev/2048/style/main.scss:196-205`, `/Users/helpingstar/dev/2048/style/main.css:159-174,592-607`   |
| Win overlay fill                        | `rgba(237, 194, 46, 0.5)`                                                                  | Derived from `$tile-gold-color`                             | `/Users/helpingstar/dev/2048/style/main.scss:237-243`, `/Users/helpingstar/dev/2048/style/main.css:196-200,629-633`   |
| Score addition text                     | `rgba(119, 110, 101, 0.9)`                                                                 | Derived from `$text-color`                                  | `/Users/helpingstar/dev/2048/style/main.scss:95-106`, `/Users/helpingstar/dev/2048/style/main.css:76-90`              |
| Tile `2` background                     | `#eee4da`                                                                                  | Exact                                                       | `/Users/helpingstar/dev/2048/style/main.css:331-333`                                                                  |
| Tile `2` text                           | `#776e65` inherited                                                                        | Derived from root color inheritance                         | `/Users/helpingstar/dev/2048/style/main.scss:24-32,317-326`, `/Users/helpingstar/dev/2048/style/main.css:2-8,324-330` |
| Tile `4` background                     | `#ede0c8`                                                                                  | Derived from SCSS `mix($tile-gold-color, $tile-color, 10%)` | `/Users/helpingstar/dev/2048/style/main.scss:352-383`, `/Users/helpingstar/dev/2048/style/main.css:334-336`           |
| Tile `4` text                           | `#776e65` inherited                                                                        | Derived                                                     | `/Users/helpingstar/dev/2048/style/main.scss:24-32,317-326`, `/Users/helpingstar/dev/2048/style/main.css:324-336`     |
| Tile `8` background                     | `#f2b179`                                                                                  | Derived from special color mix                              | `/Users/helpingstar/dev/2048/style/main.scss:339-367`, `/Users/helpingstar/dev/2048/style/main.css:337-339`           |
| Tile `16` background                    | `#f59563`                                                                                  | Derived from special color mix                              | `/Users/helpingstar/dev/2048/style/main.scss:339-367`, `/Users/helpingstar/dev/2048/style/main.css:340-342`           |
| Tile `32` background                    | `#f67c5f`                                                                                  | Derived from special color mix                              | `/Users/helpingstar/dev/2048/style/main.scss:339-367`, `/Users/helpingstar/dev/2048/style/main.css:343-345`           |
| Tile `64` background                    | `#f65e3b`                                                                                  | Derived from special color mix                              | `/Users/helpingstar/dev/2048/style/main.scss:339-367`, `/Users/helpingstar/dev/2048/style/main.css:346-348`           |
| Tile `8`..`2048` text                   | `#f9f6f2`                                                                                  | Exact for bright variants                                   | `/Users/helpingstar/dev/2048/style/main.scss:369-371`, `/Users/helpingstar/dev/2048/style/main.css:337-383`           |
| Tile `128` background                   | `#edcf72`                                                                                  | Derived                                                     | `/Users/helpingstar/dev/2048/style/main.scss:352-383`, `/Users/helpingstar/dev/2048/style/main.css:349-353`           |
| Tile `256` background                   | `#edcc61`                                                                                  | Derived                                                     | `/Users/helpingstar/dev/2048/style/main.scss:352-383`, `/Users/helpingstar/dev/2048/style/main.css:357-361`           |
| Tile `512` background                   | `#edc850`                                                                                  | Derived                                                     | `/Users/helpingstar/dev/2048/style/main.scss:352-383`, `/Users/helpingstar/dev/2048/style/main.css:365-369`           |
| Tile `1024` background                  | `#edc53f`                                                                                  | Derived                                                     | `/Users/helpingstar/dev/2048/style/main.scss:352-383`, `/Users/helpingstar/dev/2048/style/main.css:373-377`           |
| Tile `2048` background                  | `#edc22e`                                                                                  | Exact compiled output                                       | `/Users/helpingstar/dev/2048/style/main.scss:352-383`, `/Users/helpingstar/dev/2048/style/main.css:381-385`           |
| Tile `super` background                 | `#3c3a32`                                                                                  | Derived from `mix(#333, #edc22e, 95%)`                      | `/Users/helpingstar/dev/2048/style/main.scss:404-414`, `/Users/helpingstar/dev/2048/style/main.css:389-395`           |
| Tile `128` glow                         | `0 0 30px 10px rgba(243, 215, 116, 0.2381), inset 0 0 0 1px rgba(255, 255, 255, 0.14286)`  | Derived                                                     | `/Users/helpingstar/dev/2048/style/main.css:349-353`                                                                  |
| Tile `256` glow                         | `0 0 30px 10px rgba(243, 215, 116, 0.31746), inset 0 0 0 1px rgba(255, 255, 255, 0.19048)` | Derived                                                     | `/Users/helpingstar/dev/2048/style/main.css:357-360`                                                                  |
| Tile `512` glow                         | `0 0 30px 10px rgba(243, 215, 116, 0.39683), inset 0 0 0 1px rgba(255, 255, 255, 0.2381)`  | Derived                                                     | `/Users/helpingstar/dev/2048/style/main.css:365-368`                                                                  |
| Tile `1024` glow                        | `0 0 30px 10px rgba(243, 215, 116, 0.47619), inset 0 0 0 1px rgba(255, 255, 255, 0.28571)` | Derived                                                     | `/Users/helpingstar/dev/2048/style/main.css:373-376`                                                                  |
| Tile `2048` glow                        | `0 0 30px 10px rgba(243, 215, 116, 0.55556), inset 0 0 0 1px rgba(255, 255, 255, 0.33333)` | Derived                                                     | `/Users/helpingstar/dev/2048/style/main.css:381-384`                                                                  |
| Tile `2`/`4` invisible glow placeholder | `0 0 30px 10px rgba(243, 215, 116, 0), inset 0 0 0 1px rgba(255, 255, 255, 0)`             | Derived                                                     | `/Users/helpingstar/dev/2048/style/main.css:331-336`                                                                  |

## 3.2 Typography

| Usage                                | Font Family                                         | Size                                | Weight                            | Line Height                                                    | Letter Spacing    | Color                                              | Source                                                                                                                                      |
|--------------------------------------|-----------------------------------------------------|-------------------------------------|-----------------------------------|----------------------------------------------------------------|-------------------|----------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| Root body text                       | `"Clear Sans", "Helvetica Neue", Arial, sans-serif` | `18px` desktop, `15px` mobile       | normal                            | browser normal for root, paragraph text uses `1.65` multiplier | Exact not defined | `#776e65`                                          | `/Users/helpingstar/dev/2048/style/fonts/clear-sans.css:1-29`, `/Users/helpingstar/dev/2048/style/main.scss:24-32,484-486`                  |
| Title `2048`                         | inherited family                                    | `80px` desktop, `27px` mobile       | `700`                             | browser normal                                                 | Exact not defined | `#776e65` inherited                                | `/Users/helpingstar/dev/2048/style/main.scss:42-48,493-496`                                                                                 |
| Score / Best number                  | inherited family                                    | `25px`                              | `700`                             | `47px`                                                         | Exact not defined | `#ffffff`                                          | `/Users/helpingstar/dev/2048/style/main.scss:67-82`                                                                                         |
| Score / Best badge label pseudo-text | inherited family                                    | `13px`                              | inherited normal                  | `13px`                                                         | Exact not defined | `#eee4da`                                          | `/Users/helpingstar/dev/2048/style/main.scss:83-93`                                                                                         |
| Intro paragraph                      | inherited family                                    | root `18px` desktop / `15px` mobile | normal, `strong` inside uses bold | `42px` desktop, `1.65` mobile                                  | Exact not defined | `#776e65`                                          | `/Users/helpingstar/dev/2048/style/main.scss:117-121,458-462,514-519`                                                                       |
| Button text                          | inherited family                                    | root `18px` desktop / `15px` mobile | `700` via anchor rule             | `42px` inside `40px` height container                          | Exact not defined | `#f9f6f2`                                          | `/Users/helpingstar/dev/2048/style/main.scss:123-128,158-168`                                                                               |
| Overlay message title                | inherited family                                    | `60px` desktop, `30px` mobile       | `700`                             | `60px` desktop, `30px` mobile                                  | Exact not defined | `#776e65` default, `#f9f6f2` in win state          | `/Users/helpingstar/dev/2048/style/main.scss:209-217,237-239,537-543`                                                                       |
| Tile number base                     | inherited family                                    | `55px` desktop, `35px` mobile       | `700`                             | `107px` desktop, `58px` mobile                                 | Exact not defined | `#776e65` for `2/4`, `#f9f6f2` for bright variants | `/Users/helpingstar/dev/2048/style/main.scss:291-326,533-535`, `/Users/helpingstar/dev/2048/style/main.css:231-234,324-330,664-667,749-750` |
| Tile number `100`-`999`              | inherited family                                    | `45px` desktop, `25px` mobile       | `700`                             | inherits tile line height                                      | Exact not defined | `#f9f6f2` for `128/256/512`                        | `/Users/helpingstar/dev/2048/style/main.scss:384-398`, `/Users/helpingstar/dev/2048/style/main.css:349-372`                                 |
| Tile number `1000+`                  | inherited family                                    | `35px` desktop, `15px` mobile       | `700`                             | inherits tile line height                                      | Exact not defined | `#f9f6f2`                                          | `/Users/helpingstar/dev/2048/style/main.scss:392-398`, `/Users/helpingstar/dev/2048/style/main.css:373-388`                                 |
| Super tile number `>2048`            | inherited family                                    | `30px` desktop, `10px` mobile       | `700`                             | inherits tile line height                                      | Exact not defined | `#f9f6f2`                                          | `/Users/helpingstar/dev/2048/style/main.scss:404-414`, `/Users/helpingstar/dev/2048/style/main.css:389-395`                                 |
| Paragraph body text below board      | inherited family                                    | root `18px` desktop / `15px` mobile | normal; `strong`/links bold       | `1.65`                                                         | Exact not defined | `#776e65`                                          | `/Users/helpingstar/dev/2048/index.html:75-85`, `/Users/helpingstar/dev/2048/style/main.scss:117-134`                                       |
| Score addition label                 | inherited family                                    | `25px`                              | `700`                             | `25px`                                                         | Exact not defined | `rgba(119, 110, 101, 0.9)`                         | `/Users/helpingstar/dev/2048/style/main.scss:95-106`                                                                                        |

## 3.3 Spacing / Radius / Border / Elevation

| Usage                                           | Value                                                                                                                                     | Source                                                                                                                             |
|-------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------|
| Desktop container width                         | `500px` Exact                                                                                                                             | `/Users/helpingstar/dev/2048/style/main.scss:143-146`                                                                              |
| Mobile container width                          | `280px` Exact                                                                                                                             | `/Users/helpingstar/dev/2048/style/main.scss:498-501`                                                                              |
| Desktop body outer margin                       | `80px 0` Exact                                                                                                                            | `/Users/helpingstar/dev/2048/style/main.scss:34-36`                                                                                |
| Mobile body outer margin and horizontal padding | `margin: 20px 0`, `padding: 0 20px` Exact                                                                                                 | `/Users/helpingstar/dev/2048/style/main.scss:488-491`                                                                              |
| Board outer size                                | `500px x 500px` desktop, `280px x 280px` mobile                                                                                           | `/Users/helpingstar/dev/2048/style/main.scss:188-195,573-591 compiled in CSS`                                                      |
| Board padding                                   | `15px` desktop, `10px` mobile                                                                                                             | `/Users/helpingstar/dev/2048/style/main.scss:175,576`                                                                              |
| Board top margin                                | `40px` desktop, `17px` mobile                                                                                                             | `/Users/helpingstar/dev/2048/style/main.scss:19,173,482,574`                                                                       |
| Board radius                                    | `6px` Derived from `$tile-border-radius * 2`                                                                                              | `/Users/helpingstar/dev/2048/style/main.scss:8,189`                                                                                |
| Cell radius                                     | `3px` Exact                                                                                                                               | `/Users/helpingstar/dev/2048/style/main.scss:8,277`                                                                                |
| Tile radius                                     | `3px` Exact                                                                                                                               | `/Users/helpingstar/dev/2048/style/main.scss:8,318`                                                                                |
| Badge radius                                    | `3px` Exact                                                                                                                               | `/Users/helpingstar/dev/2048/style/main.scss:78`                                                                                   |
| Button radius                                   | `3px` Exact                                                                                                                               | `/Users/helpingstar/dev/2048/style/main.scss:158-168`                                                                              |
| Grid spacing                                    | `15px` desktop, `10px` mobile                                                                                                             | `/Users/helpingstar/dev/2048/style/main.scss:5,478`                                                                                |
| Background cell size                            | `106.25px` desktop, `57.5px` mobile Derived from board formula                                                                            | `/Users/helpingstar/dev/2048/style/main.scss:7,272-274,480,650-656 compiled CSS`                                                   |
| Rendered tile size                              | `107px` desktop, `58px` mobile Derived from `ceil($tile-size)`                                                                            | `/Users/helpingstar/dev/2048/style/main.scss:293-295`, `/Users/helpingstar/dev/2048/style/main.css:231-234,664-667`                |
| Tile step offset                                | `121px` desktop, `67px` and `68px` mixed effective mobile steps via floor formula outputs `0,67,135,202`                                  | `/Users/helpingstar/dev/2048/style/main.scss:299-305`, `/Users/helpingstar/dev/2048/style/main.css:235-314,668-747`                |
| Score badge padding                             | `15px 25px` desktop, `15px 10px` mobile                                                                                                   | `/Users/helpingstar/dev/2048/style/main.scss:73,503-507`                                                                           |
| Score badge min width                           | `40px` mobile only                                                                                                                        | `/Users/helpingstar/dev/2048/style/main.scss:503-507`                                                                              |
| Score badge top margin                          | `8px` desktop, `0px` mobile                                                                                                               | `/Users/helpingstar/dev/2048/style/main.scss:80,503-505`                                                                           |
| Intro/restart mobile widths                     | `55%` and `42%`                                                                                                                           | `/Users/helpingstar/dev/2048/style/main.scss:513-527`                                                                              |
| Restart button top margin mobile                | `2px`                                                                                                                                     | `/Users/helpingstar/dev/2048/style/main.scss:521-527`                                                                              |
| Overlay title top margin                        | `222px` desktop, `90px` mobile                                                                                                            | `/Users/helpingstar/dev/2048/style/main.scss:209-217,537-543`                                                                      |
| Overlay lower button group top margin           | `59px` desktop, `30px` mobile                                                                                                             | `/Users/helpingstar/dev/2048/style/main.scss:219-222,545-547`                                                                      |
| Overlay button start margin                     | `9px` on every button                                                                                                                     | `/Users/helpingstar/dev/2048/style/main.scss:224-232`                                                                              |
| Paragraph bottom margin                         | `10px`                                                                                                                                    | `/Users/helpingstar/dev/2048/style/main.scss:117-121`                                                                              |
| Explanation section top margin                  | `50px`                                                                                                                                    | `/Users/helpingstar/dev/2048/style/main.scss:471-473`                                                                              |
| Divider spacing                                 | `margin-top: 20px`, `margin-bottom: 30px`, border `1px solid #d8d4d0`                                                                     | `/Users/helpingstar/dev/2048/style/main.scss:136-141`                                                                              |
| Layout grid                                     | Fixed `4 x 4` matrix, no scrolling, no virtualization                                                                                     | `/Users/helpingstar/dev/2048/index.html:43-67`, `/Users/helpingstar/dev/2048/js/application.js:1-4`                                |
| Opacity tokens                                  | Grid cell `0.35`, neutral overlay `0.5`, win overlay `0.5`, score addition text `0.9`, tile glow alpha ramps `0.2381`..`0.55556`          | `/Users/helpingstar/dev/2048/style/main.scss:95-106,196-205,237-238,376-382`, `/Users/helpingstar/dev/2048/style/main.css:349-384` |
| Icon size token                                 | No runtime icons in gameplay UI (`Exact absence`)                                                                                         | Asset search in `/Users/helpingstar/dev/2048/index.html`, `/Users/helpingstar/dev/2048/style`, `/Users/helpingstar/dev/2048/js`    |
| Avatar / image ratio token                      | No avatar or in-board image surfaces in gameplay UI (`Exact absence`)                                                                     | Asset search in `/Users/helpingstar/dev/2048/index.html`, `/Users/helpingstar/dev/2048/style`, `/Users/helpingstar/dev/2048/js`    |
| Theme branching                                 | No dark mode, light mode toggle, seasonal theme, or event theme branch exists (`Exact absence`)                                           | `/Users/helpingstar/dev/2048/style/main.scss`, `/Users/helpingstar/dev/2048/index.html`, `/Users/helpingstar/dev/2048/js`          |
| Visible elevation                               | No global shadow. Only tile glow on `128+` values and no-op shadow placeholder on `2/4`.                                                  | `/Users/helpingstar/dev/2048/style/main.scss:376-382`, `/Users/helpingstar/dev/2048/style/main.css:331-384`                        |
| Z-index tiers                                   | `grid-container: 1`, `tile-container: 2`, `.tile-inner: 10`, `.tile-merged .tile-inner: 20`, `.score-addition: 100`, `.game-message: 100` | `/Users/helpingstar/dev/2048/style/main.scss:205,252-289,317-323,449,103`                                                          |

## 3.4 Motion

| Motion Name              | Property                                                                | Duration                    | Easing        | Trigger                                  | Source                                                                                                                         |
|--------------------------|-------------------------------------------------------------------------|-----------------------------|---------------|------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| Tile movement transition | `transform` only                                                        | `100ms`                     | `ease-in-out` | Existing tile changes position class     | `/Users/helpingstar/dev/2048/style/main.scss:22,328-333`, `/Users/helpingstar/dev/2048/js/html_actuator.js:67-72`              |
| Tile appear              | `scale 0 -> 1`, `opacity 0 -> 1`                                        | `200ms` with `100ms` delay  | `ease`        | New random tile rendered with `tile-new` | `/Users/helpingstar/dev/2048/style/main.scss:417-432`, `/Users/helpingstar/dev/2048/js/html_actuator.js:81-84`                 |
| Tile merge pop           | `scale 0 -> 1.2 -> 1`                                                   | `200ms` with `100ms` delay  | `ease`        | Merged tile rendered with `tile-merged`  | `/Users/helpingstar/dev/2048/style/main.scss:434-452`, `/Users/helpingstar/dev/2048/js/html_actuator.js:73-80`                 |
| Overlay fade-in          | `opacity 0 -> 1`                                                        | `800ms` with `1200ms` delay | `ease`        | Board enters won/over state              | `/Users/helpingstar/dev/2048/style/main.scss:148-156,234-235`, `/Users/helpingstar/dev/2048/js/html_actuator.js:27-33,127-133` |
| Score addition rise      | `top 25px -> -50px`, `opacity 1 -> 0`                                   | `600ms`                     | `ease-in`     | Score increase > 0                       | `/Users/helpingstar/dev/2048/style/main.scss:50-60,95-106`, `/Users/helpingstar/dev/2048/js/html_actuator.js:109-120`          |
| Animation fill modes     | `both` for overlay and score addition, `backwards` for new/merged tiles | N/A                         | N/A           | Preserve first/last animation frame      | `/Users/helpingstar/dev/2048/style/helpers.scss:36-46`, `/Users/helpingstar/dev/2048/style/main.scss:104-105,235,431,451`      |

# 4. Screen Specifications

## Main Gameplay Screen

### Purpose

Allow the user to play a 4x4 2048 game using keyboard or touch swipe, view current score and best
score, restart, and continue after first reaching 2048.

### Entry Points

- DOM entry: `/Users/helpingstar/dev/2048/index.html:20-85`
- App bootstrap: `/Users/helpingstar/dev/2048/js/application.js:1-4`
- State orchestration: `/Users/helpingstar/dev/2048/js/game_manager.js:1-272`
- Input binding: `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:1-143`
- DOM rendering: `/Users/helpingstar/dev/2048/js/html_actuator.js:1-138`
- Persistent best score and session restore:
  `/Users/helpingstar/dev/2048/js/local_storage_manager.js:21-63`

### Layout Tree

- `body`
- `.container`
- `.heading`
- `h1.title`
- `.scores-container`
- `.score-container`
- `.best-container`
- `.above-game`
- `p.game-intro`
- `a.restart-button`
- `.game-container`
- `.game-message`
- `p`
- `.lower`
- `a.keep-playing-button`
- `a.retry-button`
- `.grid-container`
- 4x `.grid-row`
- 4x `.grid-cell` per row
- `.tile-container`
- dynamic 0..N x `.tile.tile-{value}.tile-position-{x}-{y}[.tile-new|.tile-merged|.tile-super]`
- `.tile-inner`
- `p.game-explanation`
- `hr`
- note paragraph with link
- `hr`
- credits paragraph with links

### Visual Structure

| Element                                | Parent                 | Size                                                                                   | Padding/Margin                                                                            | Radius                                                                   | Background                                                  | Border                              | Elevation              | Z-index                | Source                                                                                                |
|----------------------------------------|------------------------|----------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|--------------------------------------------------------------------------|-------------------------------------------------------------|-------------------------------------|------------------------|------------------------|-------------------------------------------------------------------------------------------------------|
| `body`                                 | viewport               | width auto                                                                             | desktop `margin: 80px 0`; mobile `margin: 20px 0; padding: 0 20px`                        | none                                                                     | `#faf8ef`                                                   | none                                | none                   | root                   | `/Users/helpingstar/dev/2048/style/main.scss:24-36,484-491`                                           |
| `.container`                           | `body`                 | desktop `500px`; mobile `280px`                                                        | `margin: 0 auto`                                                                          | none                                                                     | transparent                                                 | none                                | none                   | auto                   | `/Users/helpingstar/dev/2048/style/main.scss:143-146,498-501`                                         |
| `.heading`                             | `.container`           | width `100%`                                                                           | mobile `margin-bottom: 10px`; desktop none                                                | none                                                                     | transparent                                                 | none                                | none                   | auto                   | `/Users/helpingstar/dev/2048/style/main.scss:38-40,509-511`                                           |
| `h1.title`                             | `.heading`             | text box auto width                                                                    | `margin: 0`; mobile `margin-top: 15px`                                                    | none                                                                     | transparent                                                 | none                                | none                   | auto                   | `/Users/helpingstar/dev/2048/style/main.scss:42-48,493-496`                                           |
| `.scores-container`                    | `.heading`             | auto width                                                                             | none                                                                                      | none                                                                     | transparent                                                 | none                                | none                   | auto                   | `/Users/helpingstar/dev/2048/style/main.scss:62-65`                                                   |
| `.score-container` / `.best-container` | `.scores-container`    | content height `25px`; total outer height `55px`; width auto, mobile `min-width: 40px` | desktop `padding: 15px 25px; margin-top: 8px`; mobile `padding: 15px 10px; margin-top: 0` | `3px`                                                                    | `#bbada0`                                                   | none                                | none                   | local                  | `/Users/helpingstar/dev/2048/style/main.scss:67-82,503-507`                                           |
| `.above-game`                          | `.container`           | width `100%`                                                                           | none                                                                                      | none                                                                     | transparent                                                 | none                                | none                   | auto                   | `/Users/helpingstar/dev/2048/style/main.scss:454-456`                                                 |
| `.game-intro`                          | `.above-game`          | desktop auto width float-left; mobile `55%` width                                      | `margin-bottom: 0`                                                                        | none                                                                     | transparent                                                 | none                                | none                   | auto                   | `/Users/helpingstar/dev/2048/style/main.scss:458-462,514-519`                                         |
| `.restart-button`                      | `.above-game`          | desktop width auto float-right; mobile `42%`                                           | desktop `padding: 0 20px`; mobile `padding: 0; margin-top: 2px`                           | `3px`                                                                    | `#8f7a66`                                                   | none                                | none                   | auto                   | `/Users/helpingstar/dev/2048/style/main.scss:158-168,464-469,521-527`                                 |
| `.game-container`                      | `.container`           | desktop `500x500`; mobile `280x280`                                                    | desktop `margin-top: 40px; padding: 15px`; mobile `margin-top: 17px; padding: 10px`       | `6px`                                                                    | `#bbada0`                                                   | none                                | none                   | local stacking root    | `/Users/helpingstar/dev/2048/style/main.scss:171-195,475-548`                                         |
| `.grid-container`                      | `.game-container`      | content box `470x470` desktop, `260x260` mobile                                        | anchored at padded content origin                                                         | none                                                                     | transparent                                                 | none                                | none                   | `1`                    | `/Users/helpingstar/dev/2048/style/main.scss:252-255`                                                 |
| `.grid-row`                            | `.grid-container`      | full row width                                                                         | desktop `margin-bottom: 15px`; mobile `10px`; last row `0`                                | none                                                                     | transparent                                                 | none                                | none                   | inherited              | `/Users/helpingstar/dev/2048/style/main.scss:257-269,641-648 compiled CSS`                            |
| `.grid-cell`                           | `.grid-row`            | desktop `106.25x106.25`; mobile `57.5x57.5`                                            | desktop `margin-right: 15px`; mobile `10px`; last cell `0`                                | `3px`                                                                    | `rgba(238, 228, 218, 0.35)`                                 | none                                | none                   | inherited              | `/Users/helpingstar/dev/2048/style/main.scss:271-284`                                                 |
| `.tile-container`                      | `.game-container`      | content box aligned to grid origin                                                     | none                                                                                      | none                                                                     | transparent                                                 | none                                | none                   | `2`                    | `/Users/helpingstar/dev/2048/style/main.scss:286-289`                                                 |
| `.tile` wrapper                        | `.tile-container`      | desktop `107x107`; mobile `58x58`                                                      | translated by class; no margin                                                            | none                                                                     | transparent                                                 | none                                | none                   | auto                   | `/Users/helpingstar/dev/2048/style/main.scss:291-305,314-333`                                         |
| `.tile-inner`                          | `.tile`                | same as wrapper                                                                        | no padding                                                                                | `3px`                                                                    | value-specific color                                        | optional glow box-shadow for `128+` | visible glow on `128+` | `10`, `20` when merged | `/Users/helpingstar/dev/2048/style/main.scss:317-415,448-451`                                         |
| `.game-message`                        | `.game-container`      | fills entire board border box                                                          | `top/right/bottom/left: 0`                                                                | inherits square edges under board radius; no independent radius declared | default `rgba(238,228,218,0.5)`; win `rgba(237,194,46,0.5)` | none                                | none                   | `100`                  | `/Users/helpingstar/dev/2048/style/main.scss:196-249`                                                 |
| `.game-message p`                      | `.game-message`        | desktop text box `60px` tall; mobile `30px`                                            | desktop `margin-top: 222px`; mobile `90px`                                                | none                                                                     | transparent                                                 | none                                | none                   | inherited              | `/Users/helpingstar/dev/2048/style/main.scss:209-217,537-543`                                         |
| `.game-message .lower`                 | `.game-message`        | auto width                                                                             | desktop `margin-top: 59px`; mobile `30px`                                                 | none                                                                     | transparent                                                 | none                                | none                   | inherited              | `/Users/helpingstar/dev/2048/style/main.scss:219-222,545-547`                                         |
| Overlay action buttons                 | `.game-message .lower` | auto width, fixed height `40px`                                                        | each button `margin-left: 9px`                                                            | `3px`                                                                    | `#8f7a66`                                                   | none                                | none                   | inherited              | `/Users/helpingstar/dev/2048/style/main.scss:224-232`                                                 |
| `p.game-explanation`                   | `.container`           | width `100%`                                                                           | `margin-top: 50px`; default `margin-bottom: 10px`                                         | none                                                                     | transparent                                                 | none                                | none                   | auto                   | `/Users/helpingstar/dev/2048/style/main.scss:117-121,471-473`                                         |
| `hr`                                   | `.container`           | width `100%`, height visual `1px` bottom rule only                                     | `margin-top: 20px; margin-bottom: 30px`                                                   | none                                                                     | transparent                                                 | `border-bottom: 1px solid #d8d4d0`  | none                   | auto                   | `/Users/helpingstar/dev/2048/style/main.scss:136-141`                                                 |
| Footer paragraphs                      | `.container`           | width `100%`                                                                           | default `margin-bottom: 10px`                                                             | none                                                                     | transparent                                                 | none                                | none                   | auto                   | `/Users/helpingstar/dev/2048/index.html:79-85`, `/Users/helpingstar/dev/2048/style/main.scss:117-128` |

### Typography

| Element                                            | Font Family                                         | Size                          | Weight                              | Line Height                    | Letter Spacing    | Color                                               | Source                                                                                                |
|----------------------------------------------------|-----------------------------------------------------|-------------------------------|-------------------------------------|--------------------------------|-------------------|-----------------------------------------------------|-------------------------------------------------------------------------------------------------------|
| `h1.title`                                         | `"Clear Sans", "Helvetica Neue", Arial, sans-serif` | `80px` desktop, `27px` mobile | `700`                               | browser normal                 | Exact not defined | `#776e65`                                           | `/Users/helpingstar/dev/2048/style/main.scss:24-32,42-48,493-496`                                     |
| `.score-container` / `.best-container` number      | inherited                                           | `25px`                        | `700`                               | `47px`                         | Exact not defined | `#ffffff`                                           | `/Users/helpingstar/dev/2048/style/main.scss:67-82`                                                   |
| `.score-container:after` / `.best-container:after` | inherited                                           | `13px`                        | inherited                           | `13px`                         | Exact not defined | `#eee4da`                                           | `/Users/helpingstar/dev/2048/style/main.scss:83-93,109-115`                                           |
| `.game-intro`                                      | inherited                                           | root `18px` / `15px` mobile   | normal                              | `42px` desktop, `1.65` mobile  | Exact not defined | `#776e65`                                           | `/Users/helpingstar/dev/2048/style/main.scss:117-121,458-462,514-519`                                 |
| `.restart-button`                                  | inherited                                           | root `18px` / `15px` mobile   | `700`                               | `42px`                         | Exact not defined | `#f9f6f2`                                           | `/Users/helpingstar/dev/2048/style/main.scss:123-128,158-168`                                         |
| `.game-message p`                                  | inherited                                           | `60px` desktop, `30px` mobile | `700`                               | same as font size              | Exact not defined | `#776e65` default, `#f9f6f2` win state              | `/Users/helpingstar/dev/2048/style/main.scss:209-217,237-239,537-543`                                 |
| `.keep-playing-button`, `.retry-button`            | inherited                                           | root `18px` / `15px` mobile   | `700`                               | `42px`                         | Exact not defined | `#f9f6f2`                                           | `/Users/helpingstar/dev/2048/style/main.scss:123-128,158-168,224-232`                                 |
| `.tile-inner` base                                 | inherited                                           | `55px` desktop, `35px` mobile | `700`                               | `107px` desktop, `58px` mobile | Exact not defined | inherited `#776e65` unless bright variant overrides | `/Users/helpingstar/dev/2048/style/main.scss:291-326,533-535`                                         |
| `.tile-128`, `.tile-256`, `.tile-512`              | inherited                                           | `45px` desktop, `25px` mobile | `700`                               | inherited tile line height     | Exact not defined | `#f9f6f2`                                           | `/Users/helpingstar/dev/2048/style/main.scss:384-391`                                                 |
| `.tile-1024`, `.tile-2048`                         | inherited                                           | `35px` desktop, `15px` mobile | `700`                               | inherited tile line height     | Exact not defined | `#f9f6f2`                                           | `/Users/helpingstar/dev/2048/style/main.scss:392-398`                                                 |
| `.tile-super`                                      | inherited                                           | `30px` desktop, `10px` mobile | `700`                               | inherited tile line height     | Exact not defined | `#f9f6f2`                                           | `/Users/helpingstar/dev/2048/style/main.scss:404-414`                                                 |
| Body paragraphs / links                            | inherited                                           | root `18px` / `15px` mobile   | normal; links bold; `<strong>` bold | `1.65`                         | Exact not defined | `#776e65`                                           | `/Users/helpingstar/dev/2048/index.html:75-85`, `/Users/helpingstar/dev/2048/style/main.scss:117-134` |

### Assets

| Element                         | Asset                                         | Path                                                                                              | Size                                | Tint    | State Variants                            | Source                                                         |
|---------------------------------|-----------------------------------------------|---------------------------------------------------------------------------------------------------|-------------------------------------|---------|-------------------------------------------|----------------------------------------------------------------|
| Global text                     | Clear Sans Light                              | `/Users/helpingstar/dev/2048/style/fonts/ClearSans-Light-webfont.woff` and fallback `.eot/.svg`   | Font asset; no CSS pixel size fixed | No tint | Weight `200` only                         | `/Users/helpingstar/dev/2048/style/fonts/clear-sans.css:1-9`   |
| Global text                     | Clear Sans Regular                            | `/Users/helpingstar/dev/2048/style/fonts/ClearSans-Regular-webfont.woff` and fallback `.eot/.svg` | Font asset                          | No tint | Weight `400`                              | `/Users/helpingstar/dev/2048/style/fonts/clear-sans.css:11-19` |
| Global text                     | Clear Sans Bold                               | `/Users/helpingstar/dev/2048/style/fonts/ClearSans-Bold-webfont.woff` and fallback `.eot/.svg`    | Font asset                          | No tint | Weight `700`                              | `/Users/helpingstar/dev/2048/style/fonts/clear-sans.css:21-29` |
| Board / cells / tiles           | None; all surfaces are CSS colors and shadows | N/A                                                                                               | N/A                                 | N/A     | Value/state changes are color/shadow only | `/Users/helpingstar/dev/2048/style/main.scss:171-452`          |
| Browser tab icon                | Favicon                                       | `/Users/helpingstar/dev/2048/favicon.ico`                                                         | `32x32` Exact                       | No tint | None                                      | `/Users/helpingstar/dev/2048/index.html:8`                     |
| iOS home icon                   | Apple touch icon                              | `/Users/helpingstar/dev/2048/meta/apple-touch-icon.png`                                           | `152x152` Exact                     | No tint | None                                      | `/Users/helpingstar/dev/2048/index.html:9`                     |
| iOS startup image (retina 480h) | Splash                                        | `/Users/helpingstar/dev/2048/meta/apple-touch-startup-image-640x920.png`                          | `640x920` Exact                     | No tint | Device-height gated                       | `/Users/helpingstar/dev/2048/index.html:11`                    |
| iOS startup image (iPhone 5+)   | Splash                                        | `/Users/helpingstar/dev/2048/meta/apple-touch-startup-image-640x1096.png`                         | `640x1096` Exact                    | No tint | Device-height gated                       | `/Users/helpingstar/dev/2048/index.html:10`                    |

### State Matrix

| Element                                                    | Default                                                                      | Hover                    | Pressed                             | Focused                  | Selected                                                                                     | Disabled        | Loading         | Error           | Source                                                                                                                                          |
|------------------------------------------------------------|------------------------------------------------------------------------------|--------------------------|-------------------------------------|--------------------------|----------------------------------------------------------------------------------------------|-----------------|-----------------|-----------------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| `.game-container`                                          | Interactive board surface, accepts swipe and document-level keyboard control | No CSS difference        | No CSS difference                   | No CSS difference        | N/A                                                                                          | Not implemented | Not implemented | Not implemented | `/Users/helpingstar/dev/2048/style/main.scss:171-195`, `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:53-127`                        |
| `.restart-button`, `.retry-button`, `.keep-playing-button` | Filled brown button with white text                                          | No dedicated hover style | Click/touch only; no pressed visual | No dedicated focus style | N/A                                                                                          | Not implemented | Not implemented | Not implemented | `/Users/helpingstar/dev/2048/style/main.scss:158-168,224-232,464-469`, `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:71-75,140-143` |
| `.game-message`                                            | `display: none`                                                              | N/A                      | N/A                                 | N/A                      | `game-won` or `game-over` class applied                                                      | N/A             | Not implemented | Not implemented | `/Users/helpingstar/dev/2048/style/main.scss:196-249`, `/Users/helpingstar/dev/2048/js/html_actuator.js:127-138`                                |
| `.game-message.game-over`                                  | Not visible until state change                                               | N/A                      | N/A                                 | N/A                      | visible, background `rgba(238,228,218,0.5)`, message `Game over!`, only retry visible        | N/A             | N/A             | N/A             | `/Users/helpingstar/dev/2048/style/main.scss:204-249`, `/Users/helpingstar/dev/2048/js/html_actuator.js:127-133`                                |
| `.game-message.game-won`                                   | Not visible until state change                                               | N/A                      | N/A                                 | N/A                      | visible, background `rgba(237,194,46,0.5)`, message `You win!`, keep-playing + retry visible | N/A             | N/A             | N/A             | `/Users/helpingstar/dev/2048/style/main.scss:237-248`, `/Users/helpingstar/dev/2048/js/html_actuator.js:127-133`                                |
| `.score-container`                                         | Shows current score and optional transient `+N` child                        | No dedicated hover style | No dedicated pressed style          | No dedicated focus style | N/A                                                                                          | N/A             | N/A             | N/A             | `/Users/helpingstar/dev/2048/style/main.scss:67-106`, `/Users/helpingstar/dev/2048/js/html_actuator.js:106-120`                                 |
| `.score-addition`                                          | Absent unless score increases                                                | N/A                      | N/A                                 | N/A                      | N/A                                                                                          | N/A             | N/A             | N/A             | `/Users/helpingstar/dev/2048/style/main.scss:95-106`, `/Users/helpingstar/dev/2048/js/html_actuator.js:114-120`                                 |
| `.tile`                                                    | Absolute tile with value class and position class                            | No hover style           | No pressed style                    | No focus style           | N/A                                                                                          | N/A             | N/A             | N/A             | `/Users/helpingstar/dev/2048/style/main.scss:291-452`, `/Users/helpingstar/dev/2048/js/html_actuator.js:52-90`                                  |
| `.tile-new`                                                | `appear` animation after `100ms` delay                                       | N/A                      | N/A                                 | N/A                      | N/A                                                                                          | N/A             | N/A             | N/A             | `/Users/helpingstar/dev/2048/style/main.scss:429-432`, `/Users/helpingstar/dev/2048/js/html_actuator.js:81-84`                                  |
| `.tile-merged`                                             | `pop` animation and elevated inner z-index `20`                              | N/A                      | N/A                                 | N/A                      | N/A                                                                                          | N/A             | N/A             | N/A             | `/Users/helpingstar/dev/2048/style/main.scss:448-452`, `/Users/helpingstar/dev/2048/js/html_actuator.js:73-80`                                  |

### Interaction Rules

| Element                | Trigger                                           | Result                                                            | Transition                                                                                  | Source                                                                                                                                   |
|------------------------|---------------------------------------------------|-------------------------------------------------------------------|---------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| Document               | `keydown` ArrowUp/Right/Down/Left                 | Emits move `0/1/2/3`                                              | Immediate logic step; DOM redraw on `requestAnimationFrame`                                 | `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:37-69`, `/Users/helpingstar/dev/2048/js/html_actuator.js:10-35`                |
| Document               | `keydown` `W/A/S/D`                               | Same directional move mapping as arrows                           | Immediate                                                                                   | `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:46-49,53-63`                                                                   |
| Document               | `keydown` `H/J/K/L`                               | Same directional move mapping as arrows                           | Immediate                                                                                   | `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:42-45,53-63`                                                                   |
| Document               | `keydown` `R` with no modifiers                   | Restart game                                                      | Clears stored game state, hides overlay, rebuilds start tiles                               | `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:65-68,130-133`, `/Users/helpingstar/dev/2048/js/game_manager.js:16-21`         |
| `.restart-button`      | `click` or touch-end event                        | Restart game                                                      | Same as `R` key                                                                             | `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:72-74,140-143`                                                                 |
| `.retry-button`        | `click` or touch-end event                        | Restart game from overlay                                         | Same as `R` key                                                                             | `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:72-73,140-143`                                                                 |
| `.keep-playing-button` | `click` or touch-end event                        | Sets `keepPlaying = true`, hides overlay, allows moves above 2048 | Overlay cleared immediately; game resumes                                                   | `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:74-75,135-137,140-143`, `/Users/helpingstar/dev/2048/js/game_manager.js:23-27` |
| `.game-container`      | Single-finger swipe with `max(absDx, absDy) > 10` | Emits dominant-axis move direction                                | Move occurs on touch-end only                                                               | `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:76-127`                                                                        |
| `.game-container`      | Multi-touch start/end                             | Ignored                                                           | No move emitted                                                                             | `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:80-84,101-105`                                                                 |
| `.game-container`      | Touch start / move                                | `preventDefault()`                                                | Stops page scroll/selection during board interaction                                        | `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:94,97-99`                                                                      |
| Game state             | Move when `isGameTerminated()` is `true`          | Ignored                                                           | No tile motion, no new random tile                                                          | `/Users/helpingstar/dev/2048/js/game_manager.js:29-32,130-135`                                                                           |
| Actuator               | Score increase                                    | Appends `.score-addition` child                                   | Animated label removed next full re-render because score container is cleared before update | `/Users/helpingstar/dev/2048/js/html_actuator.js:106-120`                                                                                |

### Animation Rules

| Element                    | Animation                                 | Duration                     | Easing        | Condition                                                               | Source                                                                                                         |
|----------------------------|-------------------------------------------|------------------------------|---------------|-------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------|
| `.tile`                    | `transform` position interpolation        | `100ms`                      | `ease-in-out` | Tile already existed and gets new `tile-position-x-y` class in next RAF | `/Users/helpingstar/dev/2048/style/main.scss:328-333`, `/Users/helpingstar/dev/2048/js/html_actuator.js:67-72` |
| `.tile-new .tile-inner`    | `appear` (`opacity 0->1`, `scale 0->1`)   | `200ms` after `100ms` delay  | `ease`        | Randomly added tile                                                     | `/Users/helpingstar/dev/2048/style/main.scss:417-432`                                                          |
| `.tile-merged .tile-inner` | `pop` (`scale 0->1.2->1`)                 | `200ms` after `100ms` delay  | `ease`        | Merge result tile                                                       | `/Users/helpingstar/dev/2048/style/main.scss:434-452`                                                          |
| `.score-addition`          | `move-up` (`top 25->-50`, `opacity 1->0`) | `600ms`                      | `ease-in`     | Positive score delta                                                    | `/Users/helpingstar/dev/2048/style/main.scss:50-60,95-106`                                                     |
| `.game-message`            | `fade-in` (`opacity 0->1`)                | `800ms` after `1200ms` delay | `ease`        | Win or game-over class applied                                          | `/Users/helpingstar/dev/2048/style/main.scss:148-156,234-235`                                                  |

### Compose Migration Notes

- Root structure: `Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFAF8EF)))` with
  centered fixed-width content wrapper using `BoxWithConstraints` and breakpoint at `520px` CSS
  equivalent. Prefer hard breakpoint, not proportional scaling.
- Heading: `Row` with `Text("2048")` aligned start and `Row` of `ScoreBadge` aligned end. On desktop
  use `Arrangement.SpaceBetween`; on mobile keep score row end-aligned and apply title top padding
  `15.dp`.
- Above-game row: desktop can use `Row(horizontalArrangement = SpaceBetween)`. Mobile needs explicit
  widths `0.55f` and `0.42f`; preserve the unused `3%` gap instead of evenly distributing.
- Board: `Box` sized `500.dp` or `280.dp`, background `#BBADA0`, rounded `6.dp`, internal padding
  `15.dp` or `10.dp`.
- Static grid: easiest parity is nested `Column` of 4 `Row`s with fixed cell sizes and exact gaps.
  Custom drawing is optional but not required.
- Dynamic tiles: use a `Box` overlay matching the padded content box; each tile is a child `Box`
  with `Modifier.offset { IntOffset(xPx, yPx) }` from a precomputed position map.
- Tile movement: use `animateIntOffsetAsState` or `Animatable<IntOffset, AnimationVector2D>` with
  `tween(100, easing = FastOutSlowInEasing substitute is not exact)`. For stricter parity, supply
  custom cubic easing equivalent to CSS `ease-in-out`.
- New and merged tile animations: animate inner content scale and alpha with `delayMillis = 100`,
  `durationMillis = 200`. Merge pop needs keyframes `0f -> 1.2f -> 1f`.
- Overlay: separate `Box` child filling board bounds with z-layer above tiles. Keep it composed but
  invisible until terminal state. Replicate CSS behavior where input is blocked immediately even
  while opacity is `0` during the `1200ms` delay.
- ScoreBadge: build explicit top label text instead of CSS pseudo-element. Use `Box` with
  top-centered small label at `10.dp` from top, large score text center-biased lower because CSS
  line-height `47px` inside `25px` content height visually pushes the number slightly downward.
- Buttons: do not use Material defaults. Use custom background `#8F7A66`, radius `3.dp`, height
  `40.dp`, no elevation, no ripple unless product intentionally diverges. Text should be vertically
  centered with slight downward bias if exact parity is required.
- State hoisting: game state should expose grid tiles, score, best score, `won`, `over`,
  `keepPlaying`. For tile animation parity, retain previous positions and merged-from pairs in UI
  state.
- Persistence: mirror local storage keys `bestScore` and `gameState`; in Android replace with
  `DataStore` or `SharedPreferences`.

# 5. Component Specifications

## ScoreBadge / Score

### Purpose

Display current accumulated score and transient positive score delta.

### Used In

- Main heading HUD on gameplay screen.

### Source Files

- `/Users/helpingstar/dev/2048/index.html:23-25`
- `/Users/helpingstar/dev/2048/style/main.scss:67-115,503-507`
- `/Users/helpingstar/dev/2048/js/html_actuator.js:106-125`

### Full Spec

| Property Category | Property                | Value                                              | Exact/Derived/Inferred        | Source                                                    |
|-------------------|-------------------------|----------------------------------------------------|-------------------------------|-----------------------------------------------------------|
| Identification    | DOM selector            | `.score-container`                                 | Exact                         | `/Users/helpingstar/dev/2048/index.html:24`               |
| Layout            | Display                 | `inline-block`                                     | Exact                         | `/Users/helpingstar/dev/2048/style/main.scss:70-71`       |
| Layout            | Positioning             | `relative`                                         | Exact                         | `/Users/helpingstar/dev/2048/style/main.scss:70`          |
| Layout            | Padding                 | `15px 25px` desktop, `15px 10px` mobile            | Exact                         | `/Users/helpingstar/dev/2048/style/main.scss:73,503-506`  |
| Layout            | Height                  | `25px` content height                              | Exact                         | `/Users/helpingstar/dev/2048/style/main.scss:68,75`       |
| Layout            | Margin top              | `8px` desktop, `0px` mobile                        | Exact                         | `/Users/helpingstar/dev/2048/style/main.scss:80,503-504`  |
| Layout            | Min width               | `40px` mobile only                                 | Exact                         | `/Users/helpingstar/dev/2048/style/main.scss:506`         |
| Visual            | Background              | `#bbada0`                                          | Exact                         | `/Users/helpingstar/dev/2048/style/main.scss:72`          |
| Visual            | Radius                  | `3px`                                              | Exact                         | `/Users/helpingstar/dev/2048/style/main.scss:78`          |
| Visual            | Border                  | none                                               | Exact                         | No border defined in source                               |
| Typography        | Main number font size   | `25px`                                             | Exact                         | `/Users/helpingstar/dev/2048/style/main.scss:74`          |
| Typography        | Main number line height | `47px`                                             | Derived from `$height + 22px` | `/Users/helpingstar/dev/2048/style/main.scss:76`          |
| Typography        | Main number weight      | `700`                                              | Exact                         | `/Users/helpingstar/dev/2048/style/main.scss:77`          |
| Typography        | Main number color       | `#ffffff`                                          | Exact                         | `/Users/helpingstar/dev/2048/style/main.scss:79`          |
| Typography        | Label text              | `"Score"`                                          | Exact                         | `/Users/helpingstar/dev/2048/style/main.scss:109-111`     |
| Typography        | Label position          | `top: 10px`, `left: 0`, `width: 100%`              | Exact                         | `/Users/helpingstar/dev/2048/style/main.scss:83-88`       |
| Typography        | Label size and color    | `13px`, `#eee4da`, uppercase                       | Exact                         | `/Users/helpingstar/dev/2048/style/main.scss:88-93`       |
| Dynamic           | Score delta child       | `.score-addition` added only when `difference > 0` | Exact                         | `/Users/helpingstar/dev/2048/js/html_actuator.js:109-120` |

### State Differences

| State           | Differences                                                                               |
|-----------------|-------------------------------------------------------------------------------------------|
| Default         | Shows current numeric score with pseudo-label `Score`; no delta child if score unchanged. |
| Positive update | Temporary `.score-addition` child appended with text `+{difference}` and animated upward. |
| Hover           | No explicit CSS change.                                                                   |
| Focused         | No explicit CSS change.                                                                   |
| Disabled        | Not implemented.                                                                          |
| Error           | Not implemented.                                                                          |

### Compose Mapping

- Use `Box` with `clip(RoundedCornerShape(3.dp))`, background `Color(0xFFBBADA0)`, and explicit
  padding.
- Place label `Text("Score")` aligned `TopCenter` with `Modifier.padding(top = 10.dp)`.
- Place number text center-aligned. For exact visual match, apply slight bottom bias instead of
  strict geometric center because CSS line-height is taller than box height.
- Drive score delta as a transient child composable using `AnimatedVisibility` or `Animatable` for
  `y` and `alpha`.

## ScoreBadge / Best

### Purpose

Display persisted best score.

### Used In

- Main heading HUD on gameplay screen.

### Source Files

- `/Users/helpingstar/dev/2048/index.html:25`
- `/Users/helpingstar/dev/2048/style/main.scss:67-115,503-507`
- `/Users/helpingstar/dev/2048/js/html_actuator.js:123-125`

### Full Spec

| Property Category | Property            | Value                           | Exact/Derived/Inferred | Source                                                                                                                     |
|-------------------|---------------------|---------------------------------|------------------------|----------------------------------------------------------------------------------------------------------------------------|
| Identification    | DOM selector        | `.best-container`               | Exact                  | `/Users/helpingstar/dev/2048/index.html:25`                                                                                |
| Layout            | Visual/layout rules | Same as `ScoreBadge / Score`    | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:67-82,503-507`                                                                |
| Typography        | Label text          | `"Best"`                        | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:113-115`                                                                      |
| Dynamic           | Value source        | `storageManager.getBestScore()` | Exact                  | `/Users/helpingstar/dev/2048/js/html_actuator.js:123-125`, `/Users/helpingstar/dev/2048/js/local_storage_manager.js:42-49` |

### State Differences

| State    | Differences                                               |
|----------|-----------------------------------------------------------|
| Default  | Static persisted best score; no transient addition child. |
| Hover    | No explicit CSS change.                                   |
| Focused  | No explicit CSS change.                                   |
| Disabled | Not implemented.                                          |

### Compose Mapping

- Reuse `ScoreBadge` composable with label `"Best"` and no delta child slot.
- Best score source belongs in persistent state holder rather than local recomposition-only state.

## ActionButton / Neutral Filled

### Purpose

Trigger restart or continue actions without visual variant differences.

### Used In

- Above-game row: `New Game`
- Overlay: `Try again`, `Keep going`

### Source Files

- `/Users/helpingstar/dev/2048/index.html:31,38-39`
- `/Users/helpingstar/dev/2048/style/main.scss:158-168,224-232,464-469,521-527`
- `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:71-75,130-143`

### Full Spec

| Property Category | Property           | Value                                              | Exact/Derived/Inferred | Source                                                             |
|-------------------|--------------------|----------------------------------------------------|------------------------|--------------------------------------------------------------------|
| Layout            | Display            | `inline-block` mixin; restart overrides to `block` | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:158-168,464-469`      |
| Layout            | Height             | `40px`                                             | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:166`                  |
| Layout            | Horizontal padding | `20px` desktop; restart mobile `0`                 | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:163,523`              |
| Layout            | Line height        | `42px`                                             | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:167`                  |
| Layout            | Radius             | `3px`                                              | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:162`                  |
| Visual            | Background         | `#8f7a66`                                          | Derived                | `/Users/helpingstar/dev/2048/style/main.scss:161`                  |
| Visual            | Border             | none                                               | Exact                  | No border defined                                                  |
| Typography        | Color              | `#f9f6f2`                                          | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:165`                  |
| Typography        | Weight             | `700` via anchor rule                              | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:123-128`              |
| Visual            | Text decoration    | `none`                                             | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:164`                  |
| Interaction       | Cursor             | `pointer` from anchor rule                         | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:123-128`              |
| Interaction       | Events             | `click` and touch-end                              | Exact                  | `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:140-143` |

### State Differences

| State    | Differences                                                              |
|----------|--------------------------------------------------------------------------|
| Default  | Brown fill with white bold text.                                         |
| Hover    | No custom hover state.                                                   |
| Pressed  | No custom pressed state.                                                 |
| Focused  | No custom focus state.                                                   |
| Hidden   | `keep-playing-button` is `display: none` until `.game-message.game-won`. |
| Disabled | Not implemented.                                                         |

### Compose Mapping

- Use one custom composable parameterized by label and visibility.
- Suppress Material default min sizes, ripple, typography, and elevation if exact parity is
  required.
- For overlay parity, keep per-button start margin `9.dp` even on the first visible button.

## BoardSurface

### Purpose

Provide the fixed square playfield container and local stacking context for background cells,
dynamic tiles, and overlay.

### Used In

- Main gameplay screen.

### Source Files

- `/Users/helpingstar/dev/2048/index.html:34-73`
- `/Users/helpingstar/dev/2048/style/main.scss:171-195,475-548`
- `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:78-127`

### Full Spec

| Property Category | Property      | Value                             | Exact/Derived/Inferred | Source                                                                                                              |
|-------------------|---------------|-----------------------------------|------------------------|---------------------------------------------------------------------------------------------------------------------|
| Layout            | Position      | `relative`                        | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:174`                                                                   |
| Layout            | Size          | `500px` desktop, `280px` mobile   | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:190-191`, `/Users/helpingstar/dev/2048/style/main.css:154-155,587-588` |
| Layout            | Padding       | `15px` desktop, `10px` mobile     | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:175,576`                                                               |
| Layout            | Margin top    | `40px` desktop, `17px` mobile     | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:173,574`                                                               |
| Visual            | Background    | `#bbada0`                         | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:188`                                                                   |
| Visual            | Radius        | `6px`                             | Derived                | `/Users/helpingstar/dev/2048/style/main.scss:189`                                                                   |
| Interaction       | Touch action  | `none`; callout/select disabled   | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:177-186`                                                               |
| Interaction       | Swipe support | Single-finger touch sequence only | Exact                  | `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:76-127`                                                   |

### State Differences

| State                     | Differences                                                                                  |
|---------------------------|----------------------------------------------------------------------------------------------|
| Default                   | Board visible, accepts swipe and document keyboard commands.                                 |
| Terminated                | Visual board unchanged until overlay class is applied; input logic blocks moves immediately. |
| Hover / Focused / Pressed | No explicit visual changes.                                                                  |

### Compose Mapping

- `Box` with fixed size, rounded background, and internal padding.
- Add `pointerInput` for swipe detection only on the board region; keep touch threshold `10px`.
- Keep internal child order: static grid first, dynamic tiles second, overlay third.

## GridCell

### Purpose

Render the 16 background slots behind active tiles.

### Used In

- 4 rows x 4 cells in board background layer.

### Source Files

- `/Users/helpingstar/dev/2048/index.html:43-67`
- `/Users/helpingstar/dev/2048/style/main.scss:252-284,475-548`

### Full Spec

| Property Category | Property       | Value                                         | Exact/Derived/Inferred | Source                                                            |
|-------------------|----------------|-----------------------------------------------|------------------------|-------------------------------------------------------------------|
| Layout            | Size           | `106.25px` desktop, `57.5px` mobile           | Derived                | `/Users/helpingstar/dev/2048/style/main.scss:7,272-273,480`       |
| Layout            | Margin end     | `15px` desktop, `10px` mobile, last child `0` | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:258-263,274,281-282` |
| Visual            | Radius         | `3px`                                         | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:277`                 |
| Visual            | Fill           | `rgba(238, 228, 218, 0.35)`                   | Derived                | `/Users/helpingstar/dev/2048/style/main.scss:279`                 |
| Layer             | Parent z-index | `1` via `.grid-container`                     | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:252-255`             |

### State Differences

| State            | Differences             |
|------------------|-------------------------|
| Default          | Static decorative slot. |
| All other states | No variants defined.    |

### Compose Mapping

- Use nested `Row`s/`Column`s with exact gap values, or draw rounded rects on `Canvas`.
- Prefer `Canvas` only if reducing node count matters; otherwise plain composables are simpler and
  exact enough.

## NumberTile / Base and Value Variants

### Purpose

Display live tile values, movement, spawn animation, and merge animation.

### Used In

- Dynamic tile layer inside board.

### Source Files

- `/Users/helpingstar/dev/2048/style/main.scss:291-452`
- `/Users/helpingstar/dev/2048/style/main.css:231-508,664-747`
- `/Users/helpingstar/dev/2048/js/html_actuator.js:49-91`
- `/Users/helpingstar/dev/2048/js/game_manager.js:112-190`

### Full Spec

| Property Category | Property             | Value                                                                                             | Exact/Derived/Inferred                | Source                                                                                                              |
|-------------------|----------------------|---------------------------------------------------------------------------------------------------|---------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| Identification    | Wrapper classes      | `tile`, `tile-{value}`, `tile-position-{x}-{y}`, optional `tile-new`, `tile-merged`, `tile-super` | Exact                                 | `/Users/helpingstar/dev/2048/js/html_actuator.js:57-84`                                                             |
| Layout            | Wrapper positioning  | `position: absolute`                                                                              | Exact                                 | `/Users/helpingstar/dev/2048/style/main.scss:314-315`                                                               |
| Layout            | Wrapper size         | `107x107` desktop, `58x58` mobile                                                                 | Derived                               | `/Users/helpingstar/dev/2048/style/main.scss:291-295`, `/Users/helpingstar/dev/2048/style/main.css:231-234,664-667` |
| Layout            | Position map desktop | `(0,0)`, `(121,0)`, `(242,0)`, `(363,0)` axes; formula `floor((106.25 + 15) * (index - 1))`       | Derived                               | `/Users/helpingstar/dev/2048/style/main.scss:299-305`, `/Users/helpingstar/dev/2048/style/main.css:235-314`         |
| Layout            | Position map mobile  | axis values `0, 67, 135, 202`; formula `floor((57.5 + 10) * (index - 1))`                         | Derived                               | `/Users/helpingstar/dev/2048/style/main.scss:299-305`, `/Users/helpingstar/dev/2048/style/main.css:668-747`         |
| Visual            | Inner radius         | `3px`                                                                                             | Exact                                 | `/Users/helpingstar/dev/2048/style/main.scss:317-318`                                                               |
| Visual            | Text alignment       | centered                                                                                          | Exact                                 | `/Users/helpingstar/dev/2048/style/main.scss:321-322`                                                               |
| Typography        | Base font size       | `55px` desktop, `35px` mobile                                                                     | Exact desktop / exact mobile override | `/Users/helpingstar/dev/2048/style/main.scss:325,533-535`                                                           |
| Motion            | Movement transition  | `100ms ease-in-out` on `transform` only                                                           | Exact                                 | `/Users/helpingstar/dev/2048/style/main.scss:328-333`                                                               |
| Layer             | Inner z-index        | `10`, merged `20`                                                                                 | Exact                                 | `/Users/helpingstar/dev/2048/style/main.scss:323,449`                                                               |

### State Differences

| State                                        | Differences                                                                                                         |
|----------------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| Default                                      | Uses value class background/text mapping and translated position class.                                             |
| New                                          | Adds `tile-new`; inner animates `appear` after `100ms` delay.                                                       |
| Merged                                       | Adds `tile-merged`; inner z-index `20` and `pop` animation after `100ms` delay.                                     |
| Super                                        | Adds `tile-super` when value `> 2048`; dark background `#3c3a32`, bright text, font `30px` desktop / `10px` mobile. |
| Hover / Pressed / Focused / Disabled / Error | No dedicated variants.                                                                                              |

### Compose Mapping

- Represent each tile as immutable UI model with `id`, `value`, `x`, `y`, `previousPosition`,
  `mergedFrom`.
- Keep wrapper offset animation separate from inner scale/alpha animation.
- Precompute exact offset maps for desktop and mobile instead of relying on fractional
  multiplication at runtime.
- Use `key(tile.id)` to keep animation identity stable. Merge rendering requires temporary presence
  of source tiles and result tile in the same frame, matching DOM recursion in `addTile`.

## NumberTile / Value Variant Table

### Purpose

Capture exact value-specific color, text, font, and glow differences.

### Used In

- `NumberTile / Base and Value Variants`

### Source Files

- `/Users/helpingstar/dev/2048/style/main.scss:334-414`
- `/Users/helpingstar/dev/2048/style/main.css:331-395`

### Full Spec

| Property Category | Property | Value                                                                                          | Exact/Derived/Inferred | Source                                               |
|-------------------|----------|------------------------------------------------------------------------------------------------|------------------------|------------------------------------------------------|
| Variant           | `2`      | background `#eee4da`, text `#776e65` inherited, font `55px`/`35px`, invisible glow placeholder | Exact + Derived        | `/Users/helpingstar/dev/2048/style/main.css:331-333` |
| Variant           | `4`      | background `#ede0c8`, text `#776e65` inherited, font `55px`/`35px`, invisible glow placeholder | Derived                | `/Users/helpingstar/dev/2048/style/main.css:334-336` |
| Variant           | `8`      | background `#f2b179`, text `#f9f6f2`, font `55px`/`35px`                                       | Derived                | `/Users/helpingstar/dev/2048/style/main.css:337-339` |
| Variant           | `16`     | background `#f59563`, text `#f9f6f2`, font `55px`/`35px`                                       | Derived                | `/Users/helpingstar/dev/2048/style/main.css:340-342` |
| Variant           | `32`     | background `#f67c5f`, text `#f9f6f2`, font `55px`/`35px`                                       | Derived                | `/Users/helpingstar/dev/2048/style/main.css:343-345` |
| Variant           | `64`     | background `#f65e3b`, text `#f9f6f2`, font `55px`/`35px`                                       | Derived                | `/Users/helpingstar/dev/2048/style/main.css:346-348` |
| Variant           | `128`    | background `#edcf72`, text `#f9f6f2`, font `45px`/`25px`, glow present                         | Derived                | `/Users/helpingstar/dev/2048/style/main.css:349-356` |
| Variant           | `256`    | background `#edcc61`, text `#f9f6f2`, font `45px`/`25px`, glow present                         | Derived                | `/Users/helpingstar/dev/2048/style/main.css:357-364` |
| Variant           | `512`    | background `#edc850`, text `#f9f6f2`, font `45px`/`25px`, glow present                         | Derived                | `/Users/helpingstar/dev/2048/style/main.css:365-372` |
| Variant           | `1024`   | background `#edc53f`, text `#f9f6f2`, font `35px`/`15px`, glow present                         | Derived                | `/Users/helpingstar/dev/2048/style/main.css:373-380` |
| Variant           | `2048`   | background `#edc22e`, text `#f9f6f2`, font `35px`/`15px`, glow present                         | Exact + Derived glow   | `/Users/helpingstar/dev/2048/style/main.css:381-388` |
| Variant           | `super`  | background `#3c3a32`, text `#f9f6f2`, font `30px`/`10px`, no glow rule                         | Derived                | `/Users/helpingstar/dev/2048/style/main.css:389-395` |

### State Differences

| State          | Differences                                                                |
|----------------|----------------------------------------------------------------------------|
| `2`/`4`        | Dark text, no visible glow, largest font.                                  |
| `8`..`64`      | Bright text, warm orange/red backgrounds, largest font.                    |
| `128`..`512`   | Bright text, golden backgrounds, reduced font `45px`/`25px`, glow enabled. |
| `1024`..`2048` | Bright text, reduced font `35px`/`15px`, glow enabled.                     |
| `super`        | Dark charcoal-gold background, smallest font, no explicit glow.            |

### Compose Mapping

- Encode tile style lookup as data table rather than branches spread through UI code.
- Glow should use custom shadow/draw effect; Material elevation is not visually equivalent.
- Font size changes are value-dependent and breakpoint-dependent; keep them in the same style table.

## GameMessageOverlay / Game Over

### Purpose

Block further play after loss and offer restart action.

### Used In

- Board overlay when no moves remain.

### Source Files

- `/Users/helpingstar/dev/2048/index.html:35-40`
- `/Users/helpingstar/dev/2048/style/main.scss:196-249,537-548`
- `/Users/helpingstar/dev/2048/js/html_actuator.js:127-138`
- `/Users/helpingstar/dev/2048/js/game_manager.js:29-32,182-189`

### Full Spec

| Property Category | Property        | Value                       | Exact/Derived/Inferred | Source                                                                                                        |
|-------------------|-----------------|-----------------------------|------------------------|---------------------------------------------------------------------------------------------------------------|
| Identification    | State class     | `.game-over`                | Exact                  | `/Users/helpingstar/dev/2048/js/html_actuator.js:128-129`                                                     |
| Layout            | Fill bounds     | `top/right/bottom/left: 0`  | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:199-203`                                                         |
| Visual            | Background      | `rgba(238, 228, 218, 0.5)`  | Derived                | `/Users/helpingstar/dev/2048/style/main.scss:204`                                                             |
| Layer             | z-index         | `100`                       | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:205`                                                             |
| Typography        | Message text    | `"Game over!"`              | Exact                  | `/Users/helpingstar/dev/2048/js/html_actuator.js:128-132`                                                     |
| Actions           | Visible buttons | `Try again` only            | Exact                  | `/Users/helpingstar/dev/2048/index.html:38-39`, `/Users/helpingstar/dev/2048/style/main.scss:229-231,246-248` |
| Motion            | Entry           | `fade-in 800ms ease 1200ms` | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:234-235`                                                         |

### State Differences

| State               | Differences                                                                         |
|---------------------|-------------------------------------------------------------------------------------|
| Hidden              | `display: none`.                                                                    |
| Visible / Game Over | `display: block`; neutral overlay fill; default body text color; retry button only. |

### Compose Mapping

- Use `AnimatedVisibility` or always-composed `Box` with delayed opacity animation.
- Even while opacity is 0 during the 1200ms delay, consume touch input on the board to match web
  behavior.

## GameMessageOverlay / Game Won

### Purpose

Pause the game at first 2048 achievement and offer continue or restart actions.

### Used In

- Board overlay when merged tile value reaches `2048`.

### Source Files

- `/Users/helpingstar/dev/2048/index.html:35-40`
- `/Users/helpingstar/dev/2048/style/main.scss:196-249,537-548`
- `/Users/helpingstar/dev/2048/js/html_actuator.js:127-138`
- `/Users/helpingstar/dev/2048/js/game_manager.js:167-170`

### Full Spec

| Property Category | Property           | Value                       | Exact/Derived/Inferred | Source                                                                                                |
|-------------------|--------------------|-----------------------------|------------------------|-------------------------------------------------------------------------------------------------------|
| Identification    | State class        | `.game-won`                 | Exact                  | `/Users/helpingstar/dev/2048/js/html_actuator.js:128-129`                                             |
| Visual            | Background         | `rgba(237, 194, 46, 0.5)`   | Derived                | `/Users/helpingstar/dev/2048/style/main.scss:237-238`                                                 |
| Typography        | Overlay text color | `#f9f6f2`                   | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:239`                                                     |
| Typography        | Message text       | `"You win!"`                | Exact                  | `/Users/helpingstar/dev/2048/js/html_actuator.js:128-132`                                             |
| Actions           | Visible buttons    | `Keep going`, `Try again`   | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:241-243`, `/Users/helpingstar/dev/2048/index.html:38-39` |
| Motion            | Entry              | `fade-in 800ms ease 1200ms` | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:234-235`                                                 |

### State Differences

| State              | Differences                                                                                                         |
|--------------------|---------------------------------------------------------------------------------------------------------------------|
| Hidden             | `display: none`.                                                                                                    |
| Visible / Game Won | Gold translucent overlay, bright text, both buttons visible.                                                        |
| Continued          | Overlay cleared by `continueGame()`, further moves allowed while `won` remains true and `keepPlaying` becomes true. |

### Compose Mapping

- Same composable as game over with variant parameters for background, text color, text, and visible
  actions.
- `keepPlaying` action must clear the overlay without resetting board contents.

## ScoreAdditionLabel

### Purpose

Show the amount gained by the last merge.

### Used In

- `ScoreBadge / Score`

### Source Files

- `/Users/helpingstar/dev/2048/style/main.scss:50-60,95-106`
- `/Users/helpingstar/dev/2048/js/html_actuator.js:109-120`

### Full Spec

| Property Category | Property        | Value                                   | Exact/Derived/Inferred | Source                                                |
|-------------------|-----------------|-----------------------------------------|------------------------|-------------------------------------------------------|
| Layout            | Position        | `absolute`, `right: 30px`               | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:95-98`   |
| Typography        | Size            | `25px`                                  | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:99-100`  |
| Typography        | Weight          | `700`                                   | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:101`     |
| Typography        | Color           | `rgba(119, 110, 101, 0.9)`              | Derived                | `/Users/helpingstar/dev/2048/style/main.scss:98-103`  |
| Layer             | z-index         | `100`                                   | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:103`     |
| Motion            | Keyframes       | `top: 25px -> -50px`, `opacity: 1 -> 0` | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:50-60`   |
| Motion            | Duration/easing | `600ms ease-in`                         | Exact                  | `/Users/helpingstar/dev/2048/style/main.scss:104-105` |

### State Differences

| State   | Differences                                  |
|---------|----------------------------------------------|
| Absent  | No DOM node when score unchanged.            |
| Present | Renders `+{difference}` and animates upward. |

### Compose Mapping

- Use transient state list or single last-delta model.
- Animate `translationY` and `alpha` simultaneously. Start at `25.dp` from badge top and end at
  `-50.dp`.

# 6. Global Navigation / Overlay / Layering Rules

- Navigation structure: no router, no hash navigation, no view stack. App bootstraps once and stays
  on the main gameplay screen. Source: `/Users/helpingstar/dev/2048/js/application.js:1-4`.
- Scene lifecycle:
  `GameManager.setup()` restores saved session if present, otherwise seeds two tiles. Source:
  `/Users/helpingstar/dev/2048/js/game_manager.js:34-59`.
- Overlay stack inside board:
  board background `0` -> static grid `1` -> dynamic tile container `2` -> tile inner content
  `10` -> merged tile inner `20` -> board overlay `100`. Source:
  `/Users/helpingstar/dev/2048/style/main.scss:205,252-289,323,449`.
- HUD stack outside board:
  score badge local overlay `score-addition` uses `z-index: 100`, but it is confined to the score
  badge stacking context and does not cover the board. Source:
  `/Users/helpingstar/dev/2048/style/main.scss:95-106`.
- Dialog priority:
  only one modal-like surface exists, `.game-message`. No nested dialogs, no tooltip portal, no
  global modal root.
- Back handling:
  browser back is not handled; Android back behavior is undefined in source and needs product
  decision in Compose.
- Win handling:
  reaching `2048` sets `won = true`; further directional input is blocked until user presses
  `Keep going` or restarts. Source: `/Users/helpingstar/dev/2048/js/game_manager.js:167-170,29-32`.
- Loss handling:
  no moves available sets `over = true`; storage is cleared and overlay appears. Source:
  `/Users/helpingstar/dev/2048/js/game_manager.js:182-189,84-89`.

# 7. Responsive / Resolution / Input Rules

- Breakpoint: single hard breakpoint at `max-width: 520px`. Source:
  `/Users/helpingstar/dev/2048/style/main.scss:10,475`.
- Desktop layout rule:
  board and container are fixed at `500px`; no scaling between large screen widths.
- Mobile layout rule:
  board and container switch to `280px`; body gains `20px` horizontal padding; title shrinks to
  `27px`; tile font base shrinks to `35px`.
- Scaling model:
  not fluid, not aspect-ratio based, not transform-scaled. It is a full token swap between desktop
  and mobile constants.
- Tile offset model:
  absolute translate positions are generated from cell size and gap, not from CSS grid/flex.
- Safe area / inset handling:
  no CSS env safe-area usage. Source code does not handle notches or system bars explicitly.
- Viewport handling:
  meta viewport sets `width=device-width`, `initial-scale=1`, `maximum-scale=1`, `user-scalable=no`,
  `minimal-ui`. Source: `/Users/helpingstar/dev/2048/index.html:15-17`.
- Touch behavior:
  `.game-container` disables selection, callout, and touch action. Source:
  `/Users/helpingstar/dev/2048/style/main.scss:177-186`.
- Mouse behavior:
  mouse click is only used on buttons. Dragging the board with a mouse has no effect.
- Keyboard behavior:
  supported keys are arrows, `W/A/S/D`, and `H/J/K/L`; `R` restarts. Modifier keys cancel move
  handling. Source: `/Users/helpingstar/dev/2048/js/keyboard_input_manager.js:37-69`.
- Gamepad behavior:
  not implemented.
- Focus navigation:
  no explicit tab order or focus styling. Anchor elements lack `href`, so keyboard focusability may
  differ by browser and should not be assumed in Compose.
- Loading / empty / error states:
  no dedicated UI states exist for these conditions.

# 8. Migration Risks / Unknowns

| Item                          | Problem                                                                                                      | Why It Matters                                                                                                           | Suggested Verification                                                                                              |
|-------------------------------|--------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| Clear Sans metric parity      | Compose default fonts will not match Clear Sans glyph width and baseline.                                    | Title width, tile number centering, and score badge vertical bias will drift visibly.                                    | Bundle Clear Sans or choose a metrically close font and run screenshot diff on title, badge, and `1024/2048` tiles. |
| Overlay delayed fade behavior | CSS makes overlay `display: block` immediately but keeps opacity at `0` for `1200ms`.                        | If Compose delays composition instead of only delaying opacity, the board will remain tappable longer than web behavior. | During manual QA, verify that input is blocked immediately after win/loss while overlay is still invisible.         |
| Tile merge rendering model    | Web actuator recursively renders source tiles and merged tile in the same frame.                             | Simplified Compose state may lose the short-lived overlap needed for accurate merge animation.                           | Capture frame-by-frame animation and compare merge sequence with web implementation.                                |
| Button visual centering       | CSS uses `height: 40px` with `line-height: 42px`.                                                            | Perfect mathematical centering in Compose may look slightly higher than web.                                             | Compare screenshot of restart and overlay buttons; add vertical offset if needed.                                   |
| Tile glow reproduction        | CSS box-shadow glow on `128+` is value-specific and not equivalent to Material elevation.                    | Wrong shadow model noticeably changes late-game board appearance.                                                        | Implement custom shadow/draw and compare `128`, `512`, `2048` tiles side by side.                                   |
| Mobile tile offset rounding   | Mobile positions use floor rounding, producing axis values `0,67,135,202` rather than a uniform `67.5` step. | Naive `cellSize + gap` multiplication in dp can misalign tiles by 1px.                                                   | Hardcode mobile offsets or round exactly after px conversion.                                                       |
| Anchor semantics              | Runtime buttons are anchors without `href`; browser accessibility semantics are weak.                        | Compose migration may unintentionally improve or alter focus/navigation behavior.                                        | Decide whether to preserve behavior or intentionally upgrade accessibility and document the divergence.             |
| Back handling                 | Web app has no back contract.                                                                                | Android users may expect system back to exit or close overlay.                                                           | Product decision required before shipping Compose version.                                                          |
| Meta assets relevance         | Apple startup images and favicon are browser shell assets, not in-game runtime UI.                           | They may be unnecessary for Compose but are part of the original package.                                                | Confirm whether Android migration scope includes launcher icon/splash parity outside gameplay UI.                   |

# 9. Compose Implementation Readiness Checklist

- [x] 모든 color token 확정
- [x] 모든 typography token 확정
- [x] 모든 radius token 확정
- [x] 모든 shadow/elevation 규칙 확정
- [x] 모든 state matrix 확정
- [x] 모든 animation spec 확정
- [x] asset 누락 없음
- [x] overlay 계층 규칙 확정
- [x] navigation 구조 확정
