export function url(
  base: string,
  params?: Record<string, string | number | boolean | undefined | null>,
): string {
  const filteredParams =
    params &&
    Object.fromEntries(
      Object.entries(params).filter(
        ([, value]) => value !== undefined && value !== null,
      ),
    );

  const queryString = new URLSearchParams(
    Object.entries(filteredParams || {}).reduce(
      (params: Record<string, string>, [key, value]) => {
        if (value !== undefined && value !== null) {
          params[key] = String(value);
        }
        return params;
      },
      {},
    ),
  ).toString();

  return `${base}?${queryString}`;
}
