'use client';

import { StarIcon } from '@phosphor-icons/react';
import * as React from 'react';

import { cn } from '@/lib/utils';

const PRECISION = 0.5;

interface RatingItemProps extends React.HTMLAttributes<HTMLLabelElement> {
  value: number;
  hoveredValue: number | null;
  point: number;
  name: string;
  readOnly?: boolean;
  disabled?: boolean;
  onMouseLeave: React.MouseEventHandler<HTMLLabelElement>;
  onValueHover: (value: number) => void;
  onValueChange?: (value: number) => void;
}

const RatingItem = ({
  value,
  point,
  hoveredValue,
  name,
  readOnly = false,
  disabled = false,
  onMouseLeave,
  onValueChange,
  onValueHover,
}: RatingItemProps) => {
  const Comp = readOnly ? 'span' : 'label';
  const ref = React.useRef<HTMLLabelElement>(null);
  const isFirstRender = React.useRef(true);
  const id = React.useId();
  const ratingIconId = `rating-icon-${id}`;
  const isInteractive = !readOnly && !disabled;
  const partialPoint = point % 1;
  const isPartialPoint = partialPoint !== 0;
  const shouldShowFilled = (hoveredValue || value) >= point;
  const partialPointWidth =
    isPartialPoint && shouldShowFilled ? `${partialPoint * 100}%` : undefined;

  const icons = React.useMemo(
    () => ({
      empty: <StarIcon className="fill-primary" />,
      full: <StarIcon weight="fill" className="fill-primary" />,
    }),
    [],
  );

  const getRatingPoint = React.useCallback(
    (event: React.MouseEvent<HTMLLabelElement>) => {
      const { left, width } = event.currentTarget.getBoundingClientRect();
      if (width === 0) return 0;
      const x = event.clientX - left;
      const fillRatio = x / width;
      const base = Math.ceil(point) - 1;
      return base + Math.ceil(fillRatio / PRECISION) * PRECISION;
    },
    [point],
  );

  const handleMouseMove = React.useCallback(
    (event: React.MouseEvent<HTMLLabelElement>) => {
      if (!isInteractive) return;
      onValueHover(getRatingPoint(event));
    },
    [isInteractive, onValueHover, getRatingPoint],
  );

  const handleClick = React.useCallback(
    (event: React.MouseEvent<HTMLLabelElement>) => {
      if (!isInteractive) return;
      const newPoint = getRatingPoint(event);
      onValueHover(0);
      onValueChange?.(newPoint === value ? 0 : newPoint);
    },
    [isInteractive, value, onValueChange, onValueHover, getRatingPoint],
  );

  React.useEffect(() => {
    if (!ref.current) return;
    if (isFirstRender.current) {
      isFirstRender.current = false;
      return;
    }
    if (value === point) {
      ref.current.focus();
    } else if (value === 0 && ref.current.parentElement?.parentElement) {
      ref.current.parentElement?.parentElement?.focus();
    }
  }, [value, point, ref]);

  return (
    <>
      <Comp
        ref={ref}
        htmlFor={`${ratingIconId}-${point}`}
        aria-label={`${point} Stars`}
        onClick={!readOnly ? handleClick : undefined}
        onMouseMove={!readOnly ? handleMouseMove : undefined}
        onMouseLeave={!readOnly ? onMouseLeave : undefined}
        className={cn(
          '[&_svg]:pointer-events-none',
          isPartialPoint &&
            'absolute top-0 left-0 overflow-hidden pointer-events-none',
          isInteractive && 'cursor-pointer',
        )}
        style={{ width: partialPointWidth }}
      >
        {!isPartialPoint && !shouldShowFilled && icons.empty}
        {shouldShowFilled && icons.full}
      </Comp>
      {!readOnly && (
        <input
          type="radio"
          id={`${ratingIconId}-${point}`}
          name={name}
          value={point}
          className="sr-only"
          tabIndex={-1}
        />
      )}
    </>
  );
};

interface RatingProps extends React.ButtonHTMLAttributes<HTMLDivElement> {
  value: number;
  name?: string;
  readOnly?: boolean;
  disabled?: boolean;
  onValueChange?: (value: number) => void;
  onValueHover?: (value: number) => void;
}

export const Rating = React.forwardRef<HTMLDivElement, RatingProps>(
  (
    {
      value,
      name,
      className,
      readOnly = false,
      disabled = false,
      onValueChange,
      onValueHover,
      ...props
    },
    ref,
  ) => {
    const id = React.useId();
    const ratingName = name ?? `rating-${id}`;
    const [hoveredValue, setHoveredValue] = React.useState<number>(0);
    const isInteractive = !readOnly && !disabled;

    const handleValueHover = React.useCallback<(value: number) => void>(
      (point) => {
        setHoveredValue(point);
        onValueHover?.(point);
      },
      [onValueHover],
    );

    const handleKeyDown = React.useCallback(
      (event: React.KeyboardEvent<HTMLDivElement>) => {
        if (!isInteractive) return;
        switch (event.key) {
          case 'ArrowRight':
          case 'ArrowUp':
            event.preventDefault();
            if (value + PRECISION > 5) {
              onValueChange?.(0);
            } else {
              onValueChange?.(value + PRECISION);
            }
            break;
          case 'ArrowLeft':
          case 'ArrowDown':
            event.preventDefault();
            if (value - PRECISION < 0) {
              onValueChange?.(5);
            } else {
              onValueChange?.(value - PRECISION);
            }
            break;
          default:
            break;
        }
      },
      [isInteractive, value, onValueChange],
    );

    const stars = React.useMemo(() => {
      return Array.from({ length: 5 }, (_, index) => ({
        key: index,
        points: Array.from({ length: Math.floor(1 / PRECISION) }).map(
          (__, i) => index + PRECISION * (i + 1),
        ),
      }));
    }, []);

    return (
      <div
        ref={ref}
        role={!readOnly ? 'radiogroup' : 'img'}
        onKeyDown={!readOnly ? handleKeyDown : undefined}
        tabIndex={!readOnly && value === 0 ? 0 : undefined}
        className={cn('flex', className)}
        aria-label={readOnly ? `${value} stars` : 'Rating'}
        aria-valuemin={0}
        aria-valuenow={value}
        aria-valuemax={5}
        {...props}
      >
        {stars.map(({ key, points }) => (
          <span
            key={key}
            className={cn(
              'relative',
              isInteractive && 'transition-transform hover:scale-110',
              disabled && 'opacity-50 cursor-not-allowed',
            )}
            aria-disabled={disabled}
            aria-hidden={readOnly}
          >
            {points.map((point) => (
              <RatingItem
                key={point}
                name={ratingName}
                disabled={disabled}
                hoveredValue={hoveredValue}
                point={point}
                readOnly={readOnly}
                value={value}
                onMouseLeave={() => setHoveredValue(0)}
                onValueHover={handleValueHover}
                onValueChange={onValueChange}
              />
            ))}
          </span>
        ))}
      </div>
    );
  },
);
Rating.displayName = 'Rating';
