/*
This file is part of NormoGram, an unofficial Telegram client.

For license and copyright information please follow the link in the repository root.
*/
#pragma once

#include "settings/settings_type.h"

namespace Settings {

[[nodiscard]] Type NormoGramId();
[[nodiscard]] bool NormoGramShowSeconds();
[[nodiscard]] bool NormoGramUseNightIcon();

} // namespace Settings
