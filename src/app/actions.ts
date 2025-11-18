"use server";

import type { WeatherData, WeatherState } from "@/lib/types";
import { z } from "zod";

// Backend API configuration
const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080";

// Remnant demo coordinates
/*
// City to coordinates mapping
const CITY_COORDS: Record<string, { lat: number; lng: number }> = {
  "london": { lat: 51.5074, lng: -0.1278 },
  "new york": { lat: 40.7128, lng: -74.0060 },
  "tokyo": { lat: 35.6762, lng: 139.6503 },
  "paris": { lat: 48.8566, lng: 2.3522 },
  "sydney": { lat: -33.8688, lng: 151.2093 },
  "toronto": { lat: 43.6532, lng: -79.3832 },
  "thunder bay": { lat: 48.3809, lng: -89.2477 },
};

// Fallback mock data generator for unknown cities
function generateMockData(city: string): WeatherData {
  const hash = city.split("").reduce((h, c) => ((h << 5) - h) + c.charCodeAt(0), 0);
  const conditions = ["Clear", "Clouds", "Rain", "Snow", "Thunderstorm", "Mist"];
  const now = new Date();

  const current = {
    city: city.split(',')[0].split(' ').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' '),
    temperature: Math.round(10 + (Math.abs(hash) % 15) + Math.sin(now.getHours()) * 5),
    condition: conditions[Math.abs(hash) % conditions.length],
    humidity: 40 + (Math.abs(hash) % 40),
    windSpeed: 5 + (Math.abs(hash) % 15),
    date: now.toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }),
  };

  const forecast = Array.from({ length: 5 }, (_, i) => ({
    day: new Date(now.getTime() + (i + 1) * 24 * 60 * 60 * 1000).toLocaleDateString('en-US', { weekday: 'short' }),
    high: current.temperature + 5 - (Math.abs(hash * (i + 1)) % 10),
    low: current.temperature - 5 - (Math.abs(hash * (i + 1)) % 5),
    condition: conditions[Math.abs(hash * (i + 1)) % conditions.length],
  }));

  return { current, forecast };
}
*/

export async function getWeather(
  prevState: WeatherState,
  formData: FormData
): Promise<WeatherState> {
  const schema = z.object({
    city: z.string().min(1, "City name cannot be empty."),
  });
  const validatedFields = schema.safeParse({
    city: formData.get("city"),
  });

  if (!validatedFields.success) {
    return {
      error: validatedFields.error.flatten().fieldErrors.city?.[0] || "Invalid city name.",
    };
  }

  const city = validatedFields.data.city;

  try {
    // Call the backend for city
    const response = await fetch(
        `${BACKEND_URL}/weather?city=${encodeURIComponent(city)}`,
        { method: "GET" }
    );

    if (!response.ok) {
      return {
        error: `Failed to fetch weather data. Backend Error (${response.status}): ${await response.text()}`,
      };
    }

    const backendData = await response.json();

    // Transform data so frontend and backend format matches
    const weatherData: WeatherData = {
      current: {
          city: backendData.current.city,
          temperature: Math.round(backendData.current.temperatureCelsius),
          condition: backendData.current.condition,
          humidity: backendData.current.humidity,
          windSpeed: Math.round(backendData.current.windSpeedMps),
          date: new Date(backendData.current.date).toLocaleDateString('en-US',
              {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'}),
          conditionDescription: backendData.conditionDescription,
      },
      forecast: backendData.forecast.slice(1,6).map((day: any) => ({
        day: new Date(day.date).toLocaleDateString('en-US', { weekday: 'short' }),
        high: Math.round(day.highTempCelsius),
        low: Math.round(day.lowTempCelsius),
        condition: day.conditionDescription,
      })),
    };

    return {
      weatherData,
      message: `Successfully fetched weather for ${weatherData.current.city}.`,
    };
  } catch (error) {
    console.error("Error fetching weather:", error);
    return {
      error: "Failed to fetch weather data. Please check backend connection.",
    };
  }
}

// Map Open-Meteo weather codes to readable conditions
// https://open-meteo.com/en/docs
export async function rateForecast(rating: number, city: string) {
    console.log(`Rating for ${city}: ${rating} stars`);
    await new Promise((resolve) => setTimeout(resolve, 300));
    return { message: `Thank you for rating the forecast for ${city}!` }
}
