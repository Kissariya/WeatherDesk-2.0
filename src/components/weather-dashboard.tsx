"use client";

import { useActionState } from "react";
import { useEffect, useRef } from "react";
import { Search } from "lucide-react";

import { getWeather } from "@/app/actions";
import type { WeatherState } from "@/lib/types";
import { Input } from "@/components/ui/input";
import { SubmitButton } from "@/components/submit-button";
import { useToast } from "@/hooks/use-toast";
import { Card, CardContent } from "@/components/ui/card";
import { WeatherResults } from "@/components/weather-results";

const initialState: WeatherState = {
  weatherData: undefined,
  error: undefined,
  message: undefined,
};

export function WeatherDashboard() {
  const [state, formAction] = useActionState(getWeather, initialState);
  const { toast } = useToast();
  const initialLoadRef = useRef(true);

  useEffect(() => {
      if (typeof window === "undefined") return;

      if(initialLoadRef.current) {
          const lastCity = localStorage.getItem("lastCity");
          if (lastCity) {
              const formData = new FormData();
              formData.append("city", lastCity);
              formAction(formData);
          }
          initialLoadRef.current = false;
      }
  }, [formAction]);

  useEffect(() => {
    if (typeof window === "undefined") return;

    if (state.error && !initialLoadRef.current) {
      toast({
        variant: "destructive",
        title: "Error",
        description: state.error,
      });
    }
    if (state.weatherData) {
      localStorage.setItem('lastCity', state.weatherData.current.city);
    }
  }, [state, toast]);

  return (
    <form action={formAction} className="space-y-6">
        <Card className="shadow-lg">
          <CardContent className="p-4">
            <div className="flex items-center gap-2">
              <div className="relative flex-grow">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground" />
                <Input
                  type="text"
                  name="city"
                  placeholder="E.g., London, New York, Tokyo"
                  className="pl-10 text-base"
                  required
                />
              </div>
              <SubmitButton>
                <Search className="h-5 w-5 md:hidden" />
                <span className="hidden md:inline">Search</span>
              </SubmitButton>
            </div>
          </CardContent>
        </Card>
        
        <WeatherResults data={state.weatherData} />
    </form>
  );
}
