'use client';

import { CaretLeftIcon, CaretRightIcon } from '@phosphor-icons/react';
import { useMemo, useState } from 'react';
import CalendarHeatmap from 'react-calendar-heatmap';

import { useGetReflectionActivities } from '@/api/__generated__/reflection/reflection';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader } from '@/components/ui/card';

interface StreaksProps {
  handle: string;
}

export default function Streaks({ handle }: StreaksProps) {
  const currentYear = new Date().getFullYear();
  const [selectedYear, setSelectedYear] = useState(currentYear);

  const { data } = useGetReflectionActivities(handle, {
    year: selectedYear.toString(),
  });

  const activities = useMemo(() => {
    if (!data?.data?.activities) return [];
    return data.data.activities;
  }, [data]);

  const selectPreviousYear = () => {
    setSelectedYear((prev) => prev - 1);
  };

  const selectNextYear = () => {
    setSelectedYear((prev) => prev + 1);
  };

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center">
          <Button onClick={selectPreviousYear} variant="ghost">
            <CaretLeftIcon />
          </Button>

          <span>{selectedYear}</span>

          <Button onClick={selectNextYear} variant="ghost">
            <CaretRightIcon />
          </Button>
        </div>
      </CardHeader>

      <CardContent>
        <CalendarHeatmap
          startDate={new Date(selectedYear, 0, 1)}
          endDate={new Date(selectedYear, 11, 31)}
          values={activities}
        />
      </CardContent>
    </Card>
  );
}
