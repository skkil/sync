/** Formats a date deterministically (fixed locale/timezone) so SSR and client output always match. */
export function formatDateStable(date: Date | string): string {
  const value = typeof date === 'string' ? new Date(date) : date;

  return value.toLocaleDateString('en-US', {
    timeZone: 'UTC',
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
}
