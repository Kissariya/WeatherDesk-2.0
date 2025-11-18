"use client";

import { WeatherDashboard } from "@/components/weather-dashboard";

export default function Home() {
  return (
    <div className="flex flex-col items-center justify-start min-h-screen bg-background text-foreground p-4 sm:p-6 md:p-8">
      <header className="w-full max-w-4xl my-8 text-center">
        <h1 className="text-4xl sm:text-5xl font-headline font-bold text-primary-foreground bg-primary py-2 px-4 rounded-lg shadow-md inline-block">
          WeatherDesk
        </h1>
        <p className="text-muted-foreground mt-2 text-lg">Your personal weather station</p>
      </header>

      <main className="w-full max-w-4xl">
        <WeatherDashboard />
      </main>
    </div>
  );
}
