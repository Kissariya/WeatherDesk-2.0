"use client";

import { useFormStatus } from "react-dom";
import { CloudSun } from "lucide-react";

import type { WeatherData } from "@/lib/types";
import { CurrentWeatherCard } from "./current-weather-card";
import { ForecastCard } from "./forecast-card";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";

interface WeatherResultsProps {
    data: WeatherData | null | undefined;
}

export function WeatherResults({ data }: WeatherResultsProps) {
    const { pending } = useFormStatus();

    if (pending) {
        return (
            <div className="space-y-6">
                <Skeleton className="h-[230px] w-full rounded-lg" />
                <Skeleton className="h-[280px] w-full rounded-lg" />
            </div>
        );
    }

    if (data) {
        return (
            <div className="space-y-6 animate-in fade-in-50 duration-500">
                <CurrentWeatherCard data={data.current} />
                <ForecastCard forecast={data.forecast} current={data.current} />
            </div>
        );
    }

    return (
        <Card className="border-dashed">
            <CardContent className="p-10 flex flex-col items-center justify-center text-center text-muted-foreground h-96">
                <CloudSun className="w-16 h-16 mb-4 text-primary" />
                <h3 className="text-lg font-semibold text-foreground">Welcome to WeatherDesk</h3>
                <p>Enter a city to get the latest weather forecast.</p>
            </CardContent>
        </Card>
    );
}
