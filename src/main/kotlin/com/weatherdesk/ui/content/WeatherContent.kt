package com.weatherdesk.ui.content

import com.weatherdesk.model.WeatherCondition

/**
 * Weather trivia, motivational quotes, and fun facts
 * Context-aware content based on current weather conditions
 */
object WeatherContent {

    /**
     * Get a random weather trivia fact based on condition
     */
    fun getWeatherTrivia(condition: WeatherCondition): String {
        val triviaList = when (condition) {
            WeatherCondition.CLEAR -> clearSkyTrivia
            WeatherCondition.RAIN, WeatherCondition.SHOWER_RAIN -> rainyTrivia
            WeatherCondition.THUNDERSTORM -> thunderstormTrivia
            WeatherCondition.SNOW -> snowTrivia
            WeatherCondition.CLOUDS, WeatherCondition.BROKEN_CLOUDS,
            WeatherCondition.FEW_CLOUDS, WeatherCondition.SCATTERED_CLOUDS -> cloudyTrivia
            WeatherCondition.MIST -> foggyTrivia
            WeatherCondition.UNKNOWN -> generalTrivia
        }
        return triviaList.random()
    }

    /**
     * Get a motivational quote based on weather
     */
    fun getMotivationalQuote(condition: WeatherCondition, temperature: Double): String {
        val quotes = when {
            temperature > 30 -> hotWeatherQuotes
            temperature < 0 -> coldWeatherQuotes
            condition in listOf(WeatherCondition.RAIN, WeatherCondition.SHOWER_RAIN, WeatherCondition.THUNDERSTORM) -> rainyQuotes
            condition == WeatherCondition.SNOW -> snowyQuotes
            condition == WeatherCondition.CLEAR -> sunnyQuotes
            else -> generalQuotes
        }
        return quotes.random()
    }

    /**
     * Get a weather-based activity suggestion
     */
    fun getActivitySuggestion(condition: WeatherCondition, temperature: Double): String {
        return when {
            condition == WeatherCondition.CLEAR && temperature in 15.0..25.0 ->
                "Perfect day for: ${outdoorActivities.random()}"
            condition == WeatherCondition.CLEAR && temperature > 25.0 ->
                "Hot day! Try: ${hotWeatherActivities.random()}"
            condition in listOf(WeatherCondition.RAIN, WeatherCondition.SHOWER_RAIN) ->
                "Rainy day idea: ${indoorActivities.random()}"
            condition == WeatherCondition.SNOW ->
                "Snowy fun: ${snowActivities.random()}"
            temperature < 0 ->
                "Bundle up and try: ${coldWeatherActivities.random()}"
            else ->
                "Today's suggestion: ${generalActivities.random()}"
        }
    }

    /**
     * Get a fun weather emoji combination
     */
    fun getWeatherEmoji(condition: WeatherCondition): String {
        return when (condition) {
            WeatherCondition.CLEAR -> listOf("☀️✨", "🌞💫", "☀️🌈", "🌤️⭐").random()
            WeatherCondition.RAIN -> listOf("🌧️💧", "☔🌦️", "💦🌂", "🌧️⚡").random()
            WeatherCondition.THUNDERSTORM -> listOf("⛈️⚡", "🌩️💥", "⛈️🌪️", "⚡☔").random()
            WeatherCondition.SNOW -> listOf("❄️⛄", "🌨️❄️", "⛷️❄️", "☃️🎿").random()
            WeatherCondition.CLOUDS -> listOf("☁️🌥️", "🌫️☁️", "☁️💭", "🌤️☁️").random()
            else -> listOf("🌍🌤️", "🌏☁️", "🌎🌈", "🌐⛅").random()
        }
    }

    /**
     * Get a weather-themed joke
     */
    fun getWeatherJoke(): String {
        return weatherJokes.random()
    }

    // Clear sky trivia
    private val clearSkyTrivia = listOf(
        "The sun produces enough energy in one second to power the entire world for 500,000 years!",
        "Sunlight takes 8 minutes and 20 seconds to travel from the Sun to Earth.",
        "The hottest temperature ever recorded on Earth was 134°F (56.7°C) in Death Valley, California.",
        "Blue light from the sun is scattered more by the atmosphere, which is why the sky appears blue.",
        "At sea level, the atmosphere presses down with a force of 14.7 pounds per square inch!",
        "Clear nights allow more heat to escape into space, making them colder than cloudy nights.",
        "The sun is about 400 times larger than the moon but also 400 times farther away!"
    )

    // Rainy weather trivia
    private val rainyTrivia = listOf(
        "A single raindrop falls at approximately 7-18 mph, depending on its size.",
        "The smell of rain is called 'petrichor' - a combination of plant oils and bacteria!",
        "The world's rainiest place is Mawsynram, India, receiving an average of 467 inches of rain annually.",
        "Rain drops are not tear-shaped! They're actually shaped more like hamburger buns.",
        "In heavy rain, you'll get less wet by running than by walking!",
        "One inch of rain over one acre of land equals 27,154 gallons of water.",
        "The sound of rain can reduce stress levels by up to 25%!"
    )

    // Thunderstorm trivia
    private val thunderstormTrivia = listOf(
        "Lightning is five times hotter than the surface of the sun at 30,000°C!",
        "Thunder is simply the sound caused by rapidly expanding air heated by lightning.",
        "The odds of being struck by lightning in your lifetime are 1 in 15,300.",
        "Lightning strikes the Earth about 8 million times per day!",
        "A single lightning bolt can light a 100-watt bulb for 3 months.",
        "Lightning can travel up to 220,000 mph and contains up to one billion volts!",
        "The longest recorded lightning bolt stretched for 440 miles across Oklahoma."
    )

    // Snow trivia
    private val snowTrivia = listOf(
        "No two snowflakes are exactly alike - each has a unique crystalline structure!",
        "Snow isn't actually white - it's translucent! It appears white due to light reflection.",
        "The world record snowfall in 24 hours was 76 inches in Silver Lake, Colorado.",
        "Snowflakes can take up to an hour to fall from the cloud to the ground.",
        "The largest recorded snowflake was 15 inches wide and 8 inches thick!",
        "Snow is a natural sound absorber - that's why it feels so quiet after it snows.",
        "It must be below 32°F (0°C) for snow to form, but the clouds must be colder than that!"
    )

    // Cloudy weather trivia
    private val cloudyTrivia = listOf(
        "Clouds can weigh more than a million pounds despite floating in the air!",
        "The highest clouds (noctilucent) form 50 miles above Earth's surface.",
        "Clouds move at different speeds depending on their altitude - from 30-100+ mph!",
        "Fog is just a cloud that forms at ground level.",
        "Cumulonimbus clouds can grow to be 40,000 feet tall!",
        "There are 10 main types of clouds, divided into three height categories.",
        "Contrails from airplanes are actually man-made clouds!"
    )

    // Foggy/misty trivia
    private val foggyTrivia = listOf(
        "Fog is technically a cloud touching the ground.",
        "The foggiest place on Earth is Grand Banks off Newfoundland, Canada.",
        "Fog can reduce visibility to less than 1 kilometer.",
        "Radiation fog forms when the ground cools at night and chills the air above it.",
        "In dense fog, light travels only short distances before scattering.",
        "Sea fog forms when warm, moist air passes over cooler water.",
        "Fog can contain up to 50 droplets per cubic centimeter!"
    )

    // General trivia
    private val generalTrivia = listOf(
        "Weather satellites orbit Earth at about 22,300 miles above the equator.",
        "The jet stream can reach speeds of over 275 mph!",
        "Earth's atmosphere is about 300 miles thick.",
        "Weather prediction accuracy has improved by 25% over the past decade.",
        "The highest temperature difference in one day was 103°F in Montana!",
        "Wind is caused by differences in atmospheric pressure.",
        "The Coriolis effect causes weather systems to rotate in different directions in each hemisphere."
    )

    // Motivational quotes - Sunny
    private val sunnyQuotes = listOf(
        "Let the sunshine in! Today is your day to shine. ☀️",
        "Keep your face to the sunshine and you cannot see the shadows. ✨",
        "Sunshine is the best medicine - get outside and soak it up!",
        "A sunny day is nature's way of saying 'You've got this!' 🌞",
        "Let today's sunshine fuel your ambitions!",
        "Clear skies ahead - it's a perfect day for new beginnings!",
        "Brighten someone's day like the sun brightens yours! 💫"
    )

    // Rainy quotes
    private val rainyQuotes = listOf(
        "Some people feel the rain, others just get wet. Make it count! ☔",
        "Let the rain wash away yesterday's worries. Fresh start ahead!",
        "Life isn't about waiting for the storm to pass - it's about dancing in the rain! 💃",
        "Rainy days are perfect for reflection and hot beverages. ☕",
        "The best time for new growth is during the rain. 🌱",
        "Don't let the weather dampen your spirits - shine from within!",
        "Every storm runs out of rain. Better days are coming! 🌈"
    )

    // Snowy quotes
    private val snowyQuotes = listOf(
        "Like snowflakes, you are unique and beautiful! ❄️",
        "Snow is nature's way of telling us to slow down and appreciate beauty.",
        "Every snowflake is different, just like every day is a new opportunity!",
        "Cold hands, warm heart - embrace the winter magic! ⛄",
        "Let it snow! Time to make magical winter memories.",
        "Snowflakes are kisses from heaven. ❄️💙",
        "Winter is the time for comfort, good food, and the touch of a friendly hand."
    )

    // Hot weather quotes
    private val hotWeatherQuotes = listOf(
        "Hot day? Keep your cool and stay hydrated! 🥤",
        "When life gives you sunshine, make lemonade! 🍋",
        "Stay cool, stay positive, stay hydrated! 💧",
        "The heat is on, but so is your determination!",
        "Summer state of mind: chill out and enjoy! 😎",
        "Too hot to handle? Nah, you've got this!",
        "Make today so hot that even the sun gets jealous! 🔥"
    )

    // Cold weather quotes
    private val coldWeatherQuotes = listOf(
        "Cold weather? Time to warm hearts with kindness! ❤️",
        "Bundle up and keep moving forward - you're unstoppable! 🧣",
        "The cold never bothered you anyway! Stay strong! 💪",
        "Warm thoughts on a cold day - you've got this!",
        "Let your inner fire keep you warm today! 🔥",
        "Cold days build character. You're becoming your best self!",
        "Embrace the chill - it makes the warmth more appreciated! ☕"
    )

    // General quotes
    private val generalQuotes = listOf(
        "Every day brings new weather and new opportunities! 🌈",
        "Don't wait for the storm to pass, learn to dance in the rain!",
        "Weather changes, but your attitude is your choice! 😊",
        "Life is full of beauty - notice it with every weather change!",
        "Make today amazing, regardless of the weather!",
        "You are the sunshine in someone's cloudy day! ☀️",
        "The only constant is change - including the weather! Embrace it!"
    )

    // Activity suggestions
    private val outdoorActivities = listOf(
        "Go for a scenic hike or nature walk 🥾",
        "Have a picnic in the park 🧺",
        "Ride a bike on a scenic trail 🚴",
        "Visit a botanical garden 🌺",
        "Try outdoor photography 📸",
        "Play frisbee or catch in the park 🥏",
        "Go birdwatching 🦜",
        "Have an outdoor workout session 💪"
    )

    private val hotWeatherActivities = listOf(
        "Go swimming or visit the beach 🏊",
        "Make homemade ice cream 🍦",
        "Have a water balloon fight 💦",
        "Visit a splash pad or water park 🌊",
        "Enjoy iced drinks in the shade 🧊",
        "Go kayaking or paddleboarding 🛶",
        "Have a sunset beach walk 🌅"
    )

    private val indoorActivities = listOf(
        "Curl up with a good book 📚",
        "Try a new recipe or bake something delicious 🍰",
        "Watch your favorite movie marathon 🎬",
        "Start a creative project or craft 🎨",
        "Practice yoga or meditation 🧘",
        "Learn a new skill online 💻",
        "Have a game night with friends or family 🎲",
        "Organize and redecorate a room 🏠"
    )

    private val snowActivities = listOf(
        "Build a snowman or snow fort ⛄",
        "Go sledding or skiing 🎿",
        "Have a snowball fight ❄️",
        "Make snow angels 👼",
        "Try ice skating ⛸️",
        "Take winter landscape photos 📷",
        "Drink hot cocoa by a fire 🔥☕"
    )

    private val coldWeatherActivities = listOf(
        "Visit a cozy café ☕",
        "Go ice skating ⛸️",
        "Take a scenic winter drive 🚗",
        "Enjoy comfort food and warm soup 🍲",
        "Visit a museum or indoor attraction 🏛️",
        "Bundle up for a brisk walk 🧣",
        "Have a spa day at home 🛁"
    )

    private val generalActivities = listOf(
        "Try something new today! 🌟",
        "Connect with a friend or loved one 💌",
        "Practice gratitude journaling ✍️",
        "Listen to your favorite music 🎵",
        "Do a random act of kindness 💝",
        "Set a new personal goal 🎯",
        "Take time for self-care 🌸"
    )

    // Weather jokes
    private val weatherJokes = listOf(
        "What did one raindrop say to the other? Two's company, three's a cloud! ☁️",
        "Why did the weather want privacy? It was changing! 🌦️",
        "What's a tornado's favorite game? Twister! 🌪️",
        "How do hurricanes see? With their eye! 👁️",
        "What falls but never hits the ground? Temperature! 🌡️",
        "Why don't meteorologists like going to parties? They always rain on the parade! 🎉",
        "What do you call it when it rains chickens and ducks? Fowl weather! 🐔",
        "How do you wrap a cloud? With a rainbow! 🌈",
        "What did the cloud say to the lightning bolt? You're shocking! ⚡",
        "Why do weather forecasters always seem optimistic? They know every cloud has a silver lining! ⛅"
    )

    /**
     * Get a fun fact of the day
     */
    fun getFunFactOfTheDay(): String {
        val allTrivia = clearSkyTrivia + rainyTrivia + thunderstormTrivia +
                        snowTrivia + cloudyTrivia + foggyTrivia + generalTrivia
        return "📚 Fun Fact: ${allTrivia.random()}"
    }

    /**
     * Get weather wisdom based on condition
     */
    fun getWeatherWisdom(condition: WeatherCondition): String {
        return when (condition) {
            WeatherCondition.CLEAR ->
                "Wisdom: 'Make hay while the sun shines!' - Use this beautiful day productively."
            WeatherCondition.RAIN, WeatherCondition.SHOWER_RAIN ->
                "Wisdom: 'Into each life some rain must fall.' - Embrace the refreshing change!"
            WeatherCondition.THUNDERSTORM ->
                "Wisdom: 'Thunder is good, thunder is impressive, but lightning does the work.' - Be the lightning!"
            WeatherCondition.SNOW ->
                "Wisdom: 'Snow provokes responses that reach right back to childhood.' - Find joy in simple things!"
            WeatherCondition.CLOUDS, WeatherCondition.BROKEN_CLOUDS ->
                "Wisdom: 'Every cloud has a silver lining.' - Look for the positive!"
            else ->
                "Wisdom: 'Climate is what we expect, weather is what we get.' - Be adaptable!"
        }
    }
}
