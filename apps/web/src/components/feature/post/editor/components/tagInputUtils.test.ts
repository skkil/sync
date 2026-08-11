import { describe, expect, it } from 'vitest';

import {
  appendTagNames,
  normalizeTagName,
  parseDelimitedTagInput,
  parsePastedTagInput,
} from './tagInputUtils';

describe('태그 입력 구분자 해석', () => {
  it('쉼표가 없으면 미확정 입력으로 유지한다', () => {
    expect(parseDelimitedTagInput('react')).toBeNull();
  });

  it('ASCII 쉼표 앞의 값을 확정하고 마지막 값을 남긴다', () => {
    expect(parseDelimitedTagInput('react,next')).toEqual({
      completedValues: ['react'],
      remainder: 'next',
    });
  });

  it('전각 쉼표와 여러 태그 붙여넣기를 처리한다', () => {
    expect(parseDelimitedTagInput('리액트，next,typescript，')).toEqual({
      completedValues: ['리액트', 'next', 'typescript'],
      remainder: '',
    });
  });

  it('쉼표가 포함된 붙여넣기는 마지막 값까지 확정한다', () => {
    expect(parsePastedTagInput('react,nextjs,typescript')).toEqual([
      'react',
      'nextjs',
      'typescript',
    ]);
  });

  it('쉼표가 없는 붙여넣기는 일반 입력으로 처리한다', () => {
    expect(parsePastedTagInput('react')).toBeNull();
  });
});

describe('태그 목록 확정', () => {
  it('태그 이름의 공백과 대소문자를 정규화한다', () => {
    expect(normalizeTagName('  React  ')).toBe('react');
  });

  it('빈 값과 이름이 같은 태그를 제외하고 원본 배열을 변경하지 않는다', () => {
    const currentTags = [{ name: 'react', isProjectTag: false }];
    const result = appendTagNames(
      currentTags,
      [' React ', '', 'Next', 'next'],
      (name) => ({ name, isProjectTag: false }),
    );

    expect(result).toEqual({
      tags: [
        { name: 'react', isProjectTag: false },
        { name: 'next', isProjectTag: false },
      ],
      limitExceeded: false,
    });
    expect(currentTags).toEqual([{ name: 'react', isProjectTag: false }]);
  });

  it('최대 개수까지만 추가하고 초과 여부를 반환한다', () => {
    const currentTags = ['one', 'two', 'three', 'four'].map((name) => ({
      name,
      isProjectTag: false,
    }));
    const result = appendTagNames(currentTags, ['five', 'six'], (name) => ({
      name,
      isProjectTag: false,
    }));

    expect(result.tags.map((tag) => tag.name)).toEqual([
      'one',
      'two',
      'three',
      'four',
      'five',
    ]);
    expect(result.limitExceeded).toBe(true);
  });

  it('후행 쉼표 없는 대량 붙여넣기도 초과 태그를 감지한다', () => {
    const pastedTagNames = parsePastedTagInput('one,two,three,four,five,six');
    expect(pastedTagNames).not.toBeNull();

    const result = appendTagNames([], pastedTagNames ?? [], (name) => ({
      name,
      isProjectTag: false,
    }));

    expect(result.tags.map((tag) => tag.name)).toEqual([
      'one',
      'two',
      'three',
      'four',
      'five',
    ]);
    expect(result.limitExceeded).toBe(true);
  });
});
