import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { WeatherIcon } from "@/components/weather-icon";
import { Wind, Droplets } from "lucide-react";

interface CurrentWeatherCardProps {
    data: {
        city: string;
        temperature: number;
        condition: string;
        conditionDescription?: string;
        humidity: number;
        windSpeed: number;
        date: string;
    };
}

export function CurrentWeatherCard({ data }: CurrentWeatherCardProps) {
  return (
    <Card className="w-full shadow-lg border-primary/20">
      <CardHeader>
        <CardTitle className="text-3xl font-bold">{data.city} - Today</CardTitle>
        <CardDescription>{data.date}</CardDescription>
      </CardHeader>

      <CardContent className="grid grid-cols-1 sm:grid-cols-2 gap-8 items-center">
        {/* Temperature*/}
        <div className="flex flex-col items-center sm:items-start text-center sm:text-left">
          <div className="flex items-start">
            <span className="text-7xl sm:text-8xl font-bold text-accent drop-shadow-md">{data.temperature}</span>
            <span className="text-2xl sm:text-3xl font-medium mt-2">°C</span>
          </div>
          <p className="text-xl text-muted-foreground capitalize">{data.conditionDescription}</p>
        </div>

        <div className="flex flex-col items-center sm:items-end space-y-4">
          <WeatherIcon condition={data.condition} className="w-24 h-24 sm:w-32 sm:h-32 text-primary drop-shadow-lg" />

          <div className="flex space-x-6 text-muted-foreground">
            <div className="flex items-center gap-2">
              <Droplets className="w-5 h-5" />
              <span>{data.humidity}%</span>
            </div>
            <div className="flex items-center gap-2">
              <Wind className="w-5 h-5" />
              <span>{data.windSpeed} km/h</span>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
