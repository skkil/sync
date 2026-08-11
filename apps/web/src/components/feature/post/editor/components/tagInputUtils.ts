export const MAXIMUM_ALLOWED_TAGS = 5;

const TAG_DELIMITER_PATTERN = /[,，]/u;

export interface ParsedTagInput {
  completedValues: string[];
  remainder: string;
}

interface NamedTag {
  name: string;
}

interface AppendTagNamesResult<T> {
  tags: T[];
  limitExceeded: boolean;
}

export function normalizeTagName(value: string): string {
  return value.trim().toLowerCase();
}

export function parseDelimitedTagInput(value: string): ParsedTagInput | null {
  const values = value.split(TAG_DELIMITER_PATTERN);
  if (values.length === 1) {
    return null;
  }

  return {
    completedValues: values.slice(0, -1),
    remainder: values.at(-1) ?? '',
  };
}

export function parsePastedTagInput(value: string): string[] | null {
  const parsed = parseDelimitedTagInput(value);
  if (!parsed) {
    return null;
  }

  return [...parsed.completedValues, parsed.remainder];
}

export function appendTagNames<T extends NamedTag>(
  currentTags: readonly T[],
  rawNames: readonly string[],
  createTag: (normalizedName: string) => T,
  maximumAllowedTags = MAXIMUM_ALLOWED_TAGS,
): AppendTagNamesResult<T> {
  const tags = [...currentTags];
  const names = new Set(tags.map((tag) => normalizeTagName(tag.name)));
  let limitExceeded = false;

  rawNames.forEach((rawName) => {
    const name = normalizeTagName(rawName);
    if (!name || names.has(name)) {
      return;
    }

    if (tags.length >= maximumAllowedTags) {
      limitExceeded = true;
      return;
    }

    tags.push(createTag(name));
    names.add(name);
  });

  return { tags, limitExceeded };
}
