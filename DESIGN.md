---
name: SYNC
description: >
  High-level design direction for SYNC — a forum-shaped team knowledge base
  built to fight knowledge rot. Clean, professional, dark-first, deep emerald
  brand. This file captures intent and patterns; exact values (color ramps,
  spacing, component states) live in code (tokens.css / theme config).
brand: deep emerald ~#0B7A5B
theme: dark-first (light supported)
type: Inter (UI + reading) · JetBrains Mono (code)
---

# SYNC — Design Direction

The high-level look, feel, and UI patterns for SYNC. Read this to understand
_how the product should behave and feel_; exact tokens are defined in code, not
here. SYNC is a search-and-retrieval + real-time system wearing a forum — the UI
has one job: make knowledge feel **findable** and **trustworthy**. Anything that
doesn't serve that gets cut.

## Principles

1. **Clean over decorated.** Flat surfaces, hairline borders, generous
   whitespace. Depth comes from tonal steps and borders, not heavy shadows.
2. **Calm density.** A tool people read for a long time — pack information
   without noise. One level of emphasis; color reserved for meaning.
3. **Green means action _and_ trust.** Emerald is the single brand color: it
   drives interaction (primary actions, active nav) and doubles as the "verified
   / fresh" signal, because freshness is the whole thesis. Only one saturated
   green per view.
4. **The signature is trust.** SYNC's memorable element is its freshness/trust
   visual language, not a font or a hero. Make that expressive; keep everything
   else disciplined.
5. **Never rely on hue alone.** Status is always color + icon + word.
6. **Plain and human.** Sentence case, active voice, the user's vocabulary.

## Look & feel

Dark-first and professional. Near-black canvas, off-white text (never pure white
on pure black), hairline low-opacity borders in place of shadows. Modest corner
radii, an 8px spacing rhythm, and room to breathe. Motion is quick and
functional — real-time arrivals fade in, never slide or bounce.

## Color

- **Brand** is a deep, slightly blue-leaning emerald (~#0B7A5B): professional
  rather than neon, and dark enough to carry white text.
- Green does **double duty** — brand (interaction) and verified/fresh (state).
  Reserve the one saturated green for the primary action; everything else stays
  neutral so green keeps its meaning.
- **Status** uses amber (aging / warning) and red (error / unanswered /
  destructive), kept clearly distinct from brand green.
- **Post types** get only a whisper of color (slate / blue / purple), never a
  filled badge — see _Post types_.
- Neutrals carry the rest. If it isn't communicating state or type, it isn't
  colored.

## Typography

`Inter` for all UI and reading; `JetBrains Mono` for code, since fenced code is
a first-class post element. Two weights only (regular + semibold). Sentence case
everywhere except @handles.

## Layout

A ~240px sidebar and a centered content column at comfortable density. Hierarchy
comes from tonal surface steps and hairline borders; shadows appear only on
things that truly float (menus, dialogs).

## Core patterns

Intent for the key surfaces — behavior and feel, not measurements.

- **Sidebar** swaps between two modes — global home and inside a project — but
  keeps the logo, the primary **New post** action, and the account row steady so
  the app feels continuous when you move between them. One bold green (the CTA);
  the active item is a quiet recessed state, not a second green block. Long
  project lists cap with "See all". Invitations live on the bell and account
  menu, not the nav — the sidebar stays navigation-only.

- **Navigation levels.** The logo always returns to global home; the project
  switcher moves sideways between projects; "Overview" goes down into one. Two
  homes — a global following-feed and a project overview — that never share a
  name.

- **Explore** is a single destination with Projects / People / Posts tabs, not
  three separate pages; the home page previews each and links in. Discovery
  cards lead with trust signals (answered count, freshness %), never follower
  vanity.

- **Post types — distinguish by anatomy, not badges.** The three types are
  structurally different, so the card's _shape_ is the signal:
  - **Short** — no title; a message-style card (avatar, name, then the body as
    the content). Low ceremony; may drop its border to sit inline in the stream.
  - **Blog / long** — headline-forward with a short dek, a cover thumbnail, and
    a reading time: the editorial silhouette.
  - **Question** — a vote/answer stat rail plus a resolution state (unanswered /
    answered / canonical): the Q&A silhouette unique to questions.

  A single small type glyph in the muted type color is the only explicit cue — a
  whisper, never a filled pill.

- **Freshness / trust — the signature.** Verification state shows as color +
  icon + word (verified / verify soon / needs review), paired with a one-click
  Verify and a freshness % on discovery cards. This is SYNC's brand moment —
  keep it perfectly consistent wherever knowledge appears.

- **Feed.** Posts from the projects and people you follow. The type silhouettes
  let you tell kinds apart at a glance, and trust signals travel with the
  content rather than living only inside a project.

- **Tags** are the primary organizing axis — quiet neutral chips, with the
  selected filter in brand green.

## Voice

Intelligent, warm, and plain. Buttons name the action and keep the name through
the flow. Empty states are invitations, not apologies. The user's things are
"your…"; confirmations are past tense ("Saved"); "I" appears only on chat / AI
surfaces, never in system UI.

## Accessibility

WCAG AA (4.5:1 text, 3:1 non-text). Status is always color + icon + label —
never color alone. Visible focus on every interactive element. Dark-mode care:
off-white on near-black, low-opacity borders for separation. Honor reduced
motion; keep touch targets large; support full keyboard navigation.
