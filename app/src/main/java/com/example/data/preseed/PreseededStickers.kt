package com.example.data.preseed

import com.example.data.model.ActionType
import com.example.data.model.CustomAnimationItem
import com.example.data.model.GestureMapping
import com.example.data.model.GestureType
import com.example.data.model.StickerItem
import com.example.data.model.UserProfile

object PreseededData {
    val defaultCustomAnimations = listOf(
        CustomAnimationItem(
            id = "jelly_bounce",
            name = "Hyper Jelly Bounce",
            baseType = "BOUNCE",
            durationMs = 600,
            minScale = 0.88f,
            maxScale = 1.15f,
            translateYDp = 10f,
            translateXDp = 0f,
            rotationAngleDeg = 4f,
            particleType = "sparkles",
            easingType = "BOUNCY",
            isPreset = true
        ),
        CustomAnimationItem(
            id = "cosmic_orbit",
            name = "Cosmic Spin & Stars",
            baseType = "SPIN",
            durationMs = 2400,
            minScale = 0.96f,
            maxScale = 1.06f,
            translateYDp = 4f,
            translateXDp = 0f,
            rotationAngleDeg = 360f,
            particleType = "stars",
            easingType = "LINEAR",
            isPreset = true
        ),
        CustomAnimationItem(
            id = "heartbeat_pulse",
            name = "Sweet Heartbeat",
            baseType = "HEARTBEAT",
            durationMs = 900,
            minScale = 0.92f,
            maxScale = 1.18f,
            translateYDp = 0f,
            translateXDp = 0f,
            rotationAngleDeg = 0f,
            particleType = "hearts",
            easingType = "BOUNCY",
            isPreset = true
        ),
        CustomAnimationItem(
            id = "sleepy_sway",
            name = "Sleepy Dream Sway",
            baseType = "SWAY",
            durationMs = 2000,
            minScale = 0.98f,
            maxScale = 1.02f,
            translateYDp = 4f,
            translateXDp = 10f,
            rotationAngleDeg = 14f,
            particleType = "zzz",
            easingType = "SMOOTH",
            isPreset = true
        ),
        CustomAnimationItem(
            id = "disco_frenzy",
            name = "Disco Pop Melody",
            baseType = "WIGGLE",
            durationMs = 420,
            minScale = 0.94f,
            maxScale = 1.10f,
            translateYDp = 6f,
            translateXDp = 8f,
            rotationAngleDeg = 12f,
            particleType = "notes",
            easingType = "SMOOTH",
            isPreset = true
        ),
        CustomAnimationItem(
            id = "cloud_float",
            name = "Heavenly Cloud Float",
            baseType = "FLOAT",
            durationMs = 1800,
            minScale = 0.98f,
            maxScale = 1.04f,
            translateYDp = 14f,
            translateXDp = 0f,
            rotationAngleDeg = 0f,
            particleType = "bubbles",
            easingType = "SMOOTH",
            isPreset = true
        ),
        CustomAnimationItem(
            id = "happy_squish",
            name = "Mochi Squish & Stretch",
            baseType = "SQUISH",
            durationMs = 700,
            minScale = 0.86f,
            maxScale = 1.14f,
            translateYDp = 8f,
            translateXDp = 0f,
            rotationAngleDeg = 6f,
            particleType = "sparkles",
            easingType = "BOUNCY",
            isPreset = true
        )
    )

    val initialProfiles = listOf(
        UserProfile("everyday", "Everyday", "Favorite", isCurrent = true, themeSetting = "STRAWBERRY_MILK"),
        UserProfile("study", "Study", "School", isCurrent = false, themeSetting = "MATCHA_CLOUD"),
        UserProfile("gaming", "Gaming", "SportsEsports", isCurrent = false, themeSetting = "RETRO_PIXEL"),
        UserProfile("work", "Work", "Work", isCurrent = false, themeSetting = "MINIMAL_CREAM"),
        UserProfile("sleep", "Sleep", "Bedtime", isCurrent = false, themeSetting = "LAVENDER_DREAM")
    )

    val defaultGestureMappings = listOf(
        GestureMapping(
            id = "triple_shake",
            gestureName = "3 Shakes",
            gestureType = GestureType.TRIPLE_SHAKE,
            actionType = ActionType.TAKE_SCREENSHOT,
            actionLabel = "Take Screenshot",
            isEnabled = true,
            activationAnimation = "HEART_BURST",
            soundEffect = "sparkle"
        ),
        GestureMapping(
            id = "four_shake",
            gestureName = "4 Shakes",
            gestureType = GestureType.FOUR_SHAKE,
            actionType = ActionType.OPEN_CAMERA,
            actionLabel = "Open Camera",
            isEnabled = true,
            activationAnimation = "HAPPY_JUMP",
            soundEffect = "bubble"
        ),
        GestureMapping(
            id = "five_shake",
            gestureName = "5 Shakes",
            gestureType = GestureType.FIVE_SHAKE,
            actionType = ActionType.OPEN_APP,
            actionTarget = "",
            actionLabel = "Open Selected Apps",
            isEnabled = true,
            activationAnimation = "SOFT_BOUNCE",
            soundEffect = "pop"
        ),
        GestureMapping(
            id = "swipe_up",
            gestureName = "Swipe Up",
            gestureType = GestureType.SWIPE_UP,
            actionType = ActionType.OPEN_RECENTS,
            actionLabel = "Open Recent Apps",
            isEnabled = true,
            activationAnimation = "HAPPY_JUMP",
            soundEffect = "pop"
        ),
        GestureMapping(
            id = "swipe_down",
            gestureName = "Swipe Down",
            gestureType = GestureType.SWIPE_DOWN,
            actionType = ActionType.OPEN_WIFI_SETTINGS,
            actionLabel = "Toggle Wi-Fi",
            isEnabled = true,
            activationAnimation = "SOFT_BOUNCE",
            soundEffect = "bubble"
        ),
        GestureMapping(
            id = "double_tap",
            gestureName = "Double Tap",
            gestureType = GestureType.DOUBLE_TAP,
            actionType = ActionType.OPEN_CAMERA,
            actionLabel = "Open Camera",
            isEnabled = true,
            activationAnimation = "HEART_BURST",
            soundEffect = "sparkle"
        ),
        GestureMapping(
            id = "long_press",
            gestureName = "Long Press",
            gestureType = GestureType.LONG_PRESS,
            actionType = ActionType.CUTELLY_OPEN_MENU,
            actionLabel = "Open CUTELLY Menu",
            isEnabled = true,
            activationAnimation = "WIGGLE",
            soundEffect = "chime"
        ),
        GestureMapping(
            id = "single_tap",
            gestureName = "Single Tap",
            gestureType = GestureType.SINGLE_TAP,
            actionType = ActionType.CUTELLY_PLAY_SOUND,
            actionLabel = "Cute Sound & Bounce",
            isEnabled = true,
            activationAnimation = "SOFT_BOUNCE",
            soundEffect = "pop"
        )
    )

    val preseededStickers = listOf(
        // Cats
        StickerItem("cat_white", "Little Cat", "Cats", primaryColorHex = "#FFFFFF", secondaryColorHex = "#FF78AB", isFavorite = true),
        StickerItem("cat_black", "Black Cat", "Cats", primaryColorHex = "#30263E", secondaryColorHex = "#FFB2CE", isFavorite = true),
        StickerItem("cat_sleepy", "Sleepy Kitty", "Cats", primaryColorHex = "#FFF9F5", secondaryColorHex = "#EAE1FF", defaultAnimation = "SLEEP"),
        StickerItem("cat_happy", "Happy Cat", "Cats", primaryColorHex = "#FFF0C8", secondaryColorHex = "#FF78AB", defaultAnimation = "HAPPY_JUMP"),
        StickerItem("cat_paw", "Cat Paw", "Cats", primaryColorHex = "#FFE1ED", secondaryColorHex = "#FF78AB", isFavorite = true),
        StickerItem("cat_fish", "Cat with Fish", "Cats", primaryColorHex = "#FFFFFF", secondaryColorHex = "#8AE5FF"),

        // Bunnies
        StickerItem("bunny_pink", "Pink Bunny", "Bunnies", primaryColorHex = "#FFE1ED", secondaryColorHex = "#FF78AB", isFavorite = true),
        StickerItem("bunny_lop", "Lop Bunny", "Bunnies", primaryColorHex = "#FFFFFF", secondaryColorHex = "#FFE3D4"),
        StickerItem("bunny_star", "Star Bunny", "Bunnies", primaryColorHex = "#FFF0C8", secondaryColorHex = "#FF78AB"),

        // Bears
        StickerItem("bear_brown", "Teddy Bear", "Bears", primaryColorHex = "#D7A276", secondaryColorHex = "#FFE3D4", isFavorite = true),
        StickerItem("bear_polar", "Polar Bear", "Bears", primaryColorHex = "#FFFFFF", secondaryColorHex = "#8AE5FF"),
        StickerItem("panda", "Panda", "Pandas", primaryColorHex = "#FFFFFF", secondaryColorHex = "#30263E", isFavorite = true),
        StickerItem("panda_bamboo", "Boba Panda", "Pandas", primaryColorHex = "#FFFFFF", secondaryColorHex = "#6EDFA3"),

        // Frogs & Amphibians
        StickerItem("frog_green", "Happy Frog", "Frogs", primaryColorHex = "#A8EAA8", secondaryColorHex = "#48A873", isFavorite = true),
        StickerItem("frog_leaf", "Froggy Hat", "Frogs", primaryColorHex = "#C1F5C1", secondaryColorHex = "#FF78AB"),

        // Ghosts
        StickerItem("ghost_cute", "Spooky Cutie", "Ghosts", primaryColorHex = "#F4F0FF", secondaryColorHex = "#FF78AB", isFavorite = true),
        StickerItem("ghost_star", "Star Ghost", "Ghosts", primaryColorHex = "#FFFFFF", secondaryColorHex = "#FFF0C8"),

        // Animals - Birds & Others
        StickerItem("penguin", "Baby Penguin", "Animals", primaryColorHex = "#30263E", secondaryColorHex = "#8AE5FF", isFavorite = true),
        StickerItem("dino", "Tiny Dino", "Animals", primaryColorHex = "#A8EAA8", secondaryColorHex = "#FF78AB", isFavorite = true),
        StickerItem("cool_dog", "Cool Doggo", "Animals", primaryColorHex = "#EAD1A6", secondaryColorHex = "#30263E"),
        StickerItem("ducky", "Rubber Duck", "Animals", primaryColorHex = "#FFE066", secondaryColorHex = "#FF8557"),

        // Food and Drinks
        StickerItem("strawberry", "Sweet Berry", "Food", primaryColorHex = "#FF6584", secondaryColorHex = "#A8EAA8", isFavorite = true),
        StickerItem("cherries", "Cherry Pair", "Food", primaryColorHex = "#FF4B6E", secondaryColorHex = "#5FB865", isFavorite = true),
        StickerItem("toast", "Smiling Toast", "Food", primaryColorHex = "#E8BA85", secondaryColorHex = "#FF78AB", isFavorite = true),
        StickerItem("iced_drink", "Boba Tea", "Food", primaryColorHex = "#F2CCA2", secondaryColorHex = "#30263E", isFavorite = true),
        StickerItem("cupcake", "Strawberry Cake", "Food", primaryColorHex = "#FFE1ED", secondaryColorHex = "#FF6584"),
        StickerItem("ice_cream", "Soft Serve", "Food", primaryColorHex = "#FFF5E6", secondaryColorHex = "#FF78AB"),
        StickerItem("matcha_cup", "Matcha Latte", "Food", primaryColorHex = "#C1EBC1", secondaryColorHex = "#48A873"),
        StickerItem("onigiri", "Rice Ball", "Food", primaryColorHex = "#FFFFFF", secondaryColorHex = "#30263E"),
        StickerItem("peach", "Sweet Peach", "Food", primaryColorHex = "#FFBFA8", secondaryColorHex = "#FF78AB"),

        // Flowers & Nature
        StickerItem("flower_pink", "Sakura Blossom", "Flowers", primaryColorHex = "#FFB2CE", secondaryColorHex = "#FFF0C8", isFavorite = true),
        StickerItem("tulip", "Red Tulip", "Flowers", primaryColorHex = "#FF6584", secondaryColorHex = "#48A873"),
        StickerItem("sunflower", "Mini Sunflower", "Flowers", primaryColorHex = "#FFD464", secondaryColorHex = "#8B572A"),
        StickerItem("sprout", "Little Seedling", "Flowers", primaryColorHex = "#A8EAA8", secondaryColorHex = "#48A873"),
        StickerItem("four_leaf", "Lucky Clover", "Flowers", primaryColorHex = "#6EDFA3", secondaryColorHex = "#DDF8E8"),

        // Hearts & Love
        StickerItem("heart_pink", "Love Heart", "Hearts", primaryColorHex = "#FF78AB", secondaryColorHex = "#FFE1ED", isFavorite = true),
        StickerItem("heart_speech", "Heart Chat", "Hearts", primaryColorHex = "#FFFFFF", secondaryColorHex = "#FF78AB"),
        StickerItem("heart_balloon", "Heart Balloon", "Hearts", primaryColorHex = "#FF659E", secondaryColorHex = "#FFE3D4"),
        StickerItem("heart_sparkle", "Glitter Heart", "Hearts", primaryColorHex = "#FFE1ED", secondaryColorHex = "#FF78AB"),

        // Stars & Moons & Sky
        StickerItem("star_yellow", "Twinkle Star", "Stars", primaryColorHex = "#FFE066", secondaryColorHex = "#FF78AB", isFavorite = true),
        StickerItem("moon_crescent", "Pastel Moon", "Stars", primaryColorHex = "#FFF0C8", secondaryColorHex = "#BCA6FF"),
        StickerItem("saturn", "Pastel Planet", "Stars", primaryColorHex = "#D0BCFF", secondaryColorHex = "#FFD464"),
        StickerItem("cloud_smiling", "Smiling Cloud", "Clouds", primaryColorHex = "#EBF7FF", secondaryColorHex = "#FF78AB", isFavorite = true),
        StickerItem("cloud_rainbow", "Rainbow Cloud", "Clouds", primaryColorHex = "#FFFFFF", secondaryColorHex = "#FF78AB"),

        // Retro Pixel
        StickerItem("pixel_heart", "8-Bit Heart", "Retro Pixel", primaryColorHex = "#FF456E", secondaryColorHex = "#FFE1ED"),
        StickerItem("pixel_star", "Pixel Star", "Retro Pixel", primaryColorHex = "#FFD447", secondaryColorHex = "#30263E"),
        StickerItem("pixel_gameboy", "Retro Gameboy", "Retro Pixel", primaryColorHex = "#D5CCE3", secondaryColorHex = "#FF659E"),
        StickerItem("pixel_potion", "Mana Potion", "Retro Pixel", primaryColorHex = "#B58CFF", secondaryColorHex = "#FFFFFF"),

        // Doodles & Accessories
        StickerItem("ribbon_bow", "Pink Bow", "Doodles", primaryColorHex = "#FF8AB5", secondaryColorHex = "#FFFFFF", isFavorite = true),
        StickerItem("camera_cute", "Pastel Camera", "Doodles", primaryColorHex = "#EAE1FF", secondaryColorHex = "#FF78AB"),
        StickerItem("music_note", "Melody Note", "Doodles", primaryColorHex = "#CDB4DB", secondaryColorHex = "#FF78AB"),
        StickerItem("sparkles", "Magic Sparkles", "Doodles", primaryColorHex = "#FFF0C8", secondaryColorHex = "#FF78AB"),
        StickerItem("magic_wand", "Star Wand", "Doodles", primaryColorHex = "#FFE1ED", secondaryColorHex = "#FFE066"),
        StickerItem("donut", "Glazed Donut", "Food", primaryColorHex = "#E59866", secondaryColorHex = "#FF78AB"),
        StickerItem("sleepy_bear", "Sleepy Bear", "Bears", primaryColorHex = "#C89F7D", secondaryColorHex = "#EAE1FF", defaultAnimation = "SLEEP"),
        StickerItem("shiba", "Shiba Inu", "Animals", primaryColorHex = "#E5A866", secondaryColorHex = "#FFFFFF")
    )
}
