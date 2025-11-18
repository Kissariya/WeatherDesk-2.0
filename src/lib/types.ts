/**
 * TypeScript type definitions for WeatherDesk
 * Provides type safety for weather data and component props
 */

/**
 * Current weather conditions
 */

export interface BackendCurrentWeather {
    city: string;
    temperatureCelsius: number;
    condition: string;
    conditionDescription:string; // e.g., "Clear", "Cloudy", "Rain"
    humidity: number; // 0-100 percentage
    windSpeedMps: number; // in kmph
    date: string; // ISO string
    latitude: number;
    longitude: number;
    isDay: boolean; // whether it is day or night
}

/**
 * Weather forecast for a single day
 */

export interface BackendDailyForecast {
  day: string; // day name, e.g, "Monday"
  highTempCelsius: number;
  lowTempCelsius: number;
  condition: string;
  conditionDescription: string;
}

/**
 * Complete weather data for a location
 */
export interface BackendWeatherData {
  current: BackendCurrentWeather;
  forecast: BackendDailyForecast[];
}

// Used by Weather UI
export interface WeatherData {
    current: {
        city: string;
        temperature: number;
        condition: string;
        conditionDescription: string;
        humidity: number;
        windSpeed: number;
        date: string;
    };
    forecast: {
        day: string;
        high: number;
        low: number;
        condition: string;
    }[];
}

/**
 * State for weather form submission
 */
export interface WeatherState {
  weatherData?: WeatherData | null;
  error?: string | null;
  message?: string | null;
}

/**
 * Backend API response from WeatherDesk23
 */
export interface BackendWeatherResponse {
  longitude: number;
  latitude: number;
  current: {
    temperature_2m: number;
    relative_humidity_2m: number;
    is_day: number;
    wind_speed_10m: number;
  };
}

/**
 * City coordinates mapping
 */
export interface CityCoordinates {
  lat: number;
  lng: number;
}

/**
 * Weather dashboard component props
 */
export interface WeatherDashboardProps {
  initialState?: WeatherState;
}

/**
 * Current weather card component props
 */
export interface CurrentWeatherCardProps {
  data: BackendCurrentWeather;
}

/**
 * Forecast card component props
 */
export interface ForecastCardProps {
  data: BackendDailyForecast;
}

/**
 * Error response structure
 */
export interface ErrorResponse {
  error: string;
  timestamp?: string;
  details?: Record<string, unknown>;
}

/**
 * Success response structure
 */
export interface SuccessResponse<T> {
  data: T;
  message?: string;
  timestamp?: string;
}

/**
 * API response type (either success or error)
 */
export type ApiResponse<T> = SuccessResponse<T> | ErrorResponse;

/**
 * Supported weather conditions
 */
export type WeatherCondition =
  | "Clear"
  | "Cloudy"
  | "Overcast"
  | "Mist"
  | "Drizzle"
  | "Rain"
  | "Snow"
  | "Thunderstorm";

/**
 * Configuration for API calls
 */
export interface ApiConfig {
  baseUrl: string;
  timeout: number;
  retryAttempts: number;
}

/**
 * Rating submission data
 */
export interface RatingData {
  rating: number; // 1-5 stars
  city: string;
  timestamp: Date;
  feedback?: string;
}
