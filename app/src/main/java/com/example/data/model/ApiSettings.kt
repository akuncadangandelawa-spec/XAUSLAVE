package com.example.data.model

data class ApiSettings(
    val baseUrl: String = "https://api.openai.com/v1",
    val apiKey: String = "",
    val model: String = "gpt-4o-mini",
    val systemPrompt: String = DEFAULT_SYSTEM_PROMPT,
    val temperature: Float = 0.7f
) {
    companion object {
        const val DEFAULT_SYSTEM_PROMPT =
            "Anda adalah analis keuangan dan asisten trading profesional ahli instrumen XAU/USD (Gold). " +
            "Berikan analisis teknikal objektif (support/resistance, pola candlestick, tren, indikator), " +
            "analisis fundamental (data inflasi AS, suku bunga The Fed, geopolitik), " +
            "serta rekomendasi money management yang bijak (stop loss, take profit, risk:reward). " +
            "Format jawaban dengan rapi, poin-poin jelas, dan bahasa Indonesia yang mudah dipahami."
    }
}
