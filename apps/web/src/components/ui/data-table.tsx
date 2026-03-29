'use client';

import {
  Column,
  ColumnDef,
  flexRender,
  getCoreRowModel,
  useReactTable,
} from '@tanstack/react-table';
import * as React from 'react';

import { PaginationNext, PaginationPrevious } from '@/components/ui/pagination';
import {
  Table,
  TableBody,
  TableCell,
  TableCellSkeleton,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { cn } from '@/lib/utils';
import { Translations } from '@/types/i18n';

interface DataTableProps<TData, TValue> {
  columns: ColumnDef<TData, TValue>[];
  data: TData[];
  t: Translations;
  onRowClick?: (row: TData) => void;
  pageCount?: number;
  onPageChange?: (page: number) => void;
  currentPage?: number;
  hasPreviousPage?: boolean;
  hasNextPage?: boolean;
}

export function DataTable<TData, TValue>({
  columns,
  data,
  t,
  onRowClick,
  pageCount,
  onPageChange,
  currentPage,
  hasPreviousPage,
  hasNextPage,
  isLoading,
}: DataTableProps<TData, TValue> & {
  isLoading?: boolean;
}) {
  // eslint-disable-next-line react-hooks/incompatible-library
  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    manualPagination: true,
    pageCount: pageCount ?? -1,
    meta: {
      t,
    },
  });

  const handlePreviousPage = () => {
    if (onPageChange && currentPage !== undefined) {
      onPageChange(currentPage - 1);
    } else {
      table.previousPage();
    }
  };

  const handleNextPage = () => {
    if (onPageChange && currentPage !== undefined) {
      onPageChange(currentPage + 1);
    } else {
      table.nextPage();
    }
  };

  const canPreviousPage = hasPreviousPage ?? false;
  const canNextPage = hasNextPage ?? false;
  const showPagination = hasPreviousPage || hasNextPage;

  return (
    <div>
      <div className="overflow-hidden rounded-md border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => {
                  return (
                    <TableHead key={header.id}>
                      {header.isPlaceholder
                        ? null
                        : flexRender(
                            header.column.columnDef.header,
                            header.getContext(),
                          )}
                    </TableHead>
                  );
                })}
              </TableRow>
            ))}
          </TableHeader>
          <TableBody>
            {isLoading ? (
              Array.from({ length: 3 }).map((_, index) => (
                <TableRow key={index}>
                  {columns.map((_column, ci) => (
                    <TableCellSkeleton key={ci} />
                  ))}
                </TableRow>
              ))
            ) : table.getRowModel().rows?.length ? (
              table.getRowModel().rows.map((row) => (
                <TableRow
                  key={row.id}
                  data-state={row.getIsSelected() && 'selected'}
                  onClick={() => onRowClick?.(row.original)}
                >
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id}>
                      {flexRender(
                        cell.column.columnDef.cell,
                        cell.getContext(),
                      )}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell
                  colSpan={columns.length}
                  className="h-24 text-center"
                >
                  {t('empty')}
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

      {showPagination && (
        <div className="flex items-center justify-center space-x-2 py-4">
          <PaginationPrevious
            onClick={handlePreviousPage}
            disabled={!canPreviousPage}
          />

          <PaginationNext onClick={handleNextPage} disabled={!canNextPage} />
        </div>
      )}
    </div>
  );
}

interface DataTableColumnHeaderProps<
  TData,
  TValue,
> extends React.HTMLAttributes<HTMLDivElement> {
  align?: 'start' | 'center' | 'end';
  column: Column<TData, TValue>;
  title: string;
}

export function DataTableColumnHeader<TData, TValue>({
  title,
  icon,
  align = 'center',
  ...props
}: DataTableColumnHeaderProps<TData, TValue> & {
  icon?: React.ReactNode;
}) {
  return (
    <div
      className={cn(
        'flex',
        align === 'start' && 'justify-start',
        align === 'center' && 'justify-center',
        align === 'end' && 'justify-end',
      )}
    >
      <div className="flex items-center gap-2" {...props}>
        {icon && icon}
        {title}
      </div>
    </div>
  );
}
