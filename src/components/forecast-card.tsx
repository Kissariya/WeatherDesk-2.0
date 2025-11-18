import type { WeatherData } from "@/lib/types"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { WeatherIcon } from "./weather-icon";
import { Rating } from "./rating";
import { Separator } from "./ui/separator";

type ForecastCardProps = {
  forecast: WeatherData["forecast"];
  current: WeatherData["current"];
};

function ForecastItem({ day, condition, high, low }: WeatherData["forecast"][number]) {
  return (
    <div className="flex flex-col items-center gap-2 p-3 bg-secondary/30 rounded-lg text-center">
      <p className="font-semibold text-sm">{day}</p>
      <WeatherIcon condition={condition} className="w-10 h-10 text-primary" />
      <div className="flex gap-2 text-sm">
        <span className="font-bold">{high}°</span>
        <span className="text-muted-foreground">{low}°</span>
      </div>
    </div>
  );
}

export function ForecastCard({ forecast, current }: ForecastCardProps) {
  return (
    <Card className="w-full shadow-lg border-primary/20">
      <CardHeader>
        <CardTitle>5-Day Forecast</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-3 md:grid-cols-5 gap-3">
          {forecast.map((dayForecast) => (
            <ForecastItem key={dayForecast.day} {...dayForecast} />
          ))}
        </div>
        <Separator className="my-6" />
        <Rating city={current.city} />
      </CardContent>
    </Card>
  );
}
