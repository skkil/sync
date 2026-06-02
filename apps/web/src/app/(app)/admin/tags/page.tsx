'use client';

import { useTranslations } from 'next-intl';
import { useState } from 'react';

import {
  getGetAllTagsQueryOptions,
  useDeleteTag,
  useGetAllTags,
  useUpdateTag,
} from '@/api/__generated__/tag/tag';
import type { GetTagsResponseTagsItem } from '@/api/__generated__/types';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Textarea } from '@/components/ui/textarea';

export default function AdminTagsPage() {
  const t = useTranslations('pages.admin.tags');

  const [editingTag, setEditingTag] = useState<GetTagsResponseTagsItem | null>(
    null,
  );
  const [editDescription, setEditDescription] = useState('');
  const [editVerified, setEditVerified] = useState(false);

  const { data, isLoading, refetch } = useGetAllTags();
  const tags = data?.data.tags || [];

  const { mutate: updateTag } = useUpdateTag({
    mutation: {
      onSuccess: (_data, _variables, _onMutateResult, context) => {
        context.client.invalidateQueries(getGetAllTagsQueryOptions());
        setEditingTag(null);
      },
    },
  });

  const deleteTagMutation = useDeleteTag({
    mutation: {
      onSuccess: (_data, _variables, _onMutateResult, context) => {
        context.client.invalidateQueries(getGetAllTagsQueryOptions());
      },
    },
  });

  const deleteTag = async (tagId: number) => {
    if (!confirm(t('delete-confirm'))) {
      return;
    }

    deleteTagMutation.mutate({ tagId: tagId.toString() });
  };

  const openEditDialog = (tag: GetTagsResponseTagsItem) => {
    setEditingTag(tag);
    setEditDescription(tag.description);
    setEditVerified(tag.verified);
  };

  const handleSaveEdit = () => {
    if (!editingTag) return;

    updateTag({
      tagId: editingTag.id.toString(),
      data: {
        description: editDescription,
        verified: editVerified,
      },
    });
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">{t('title')}</h1>
          <p className="text-muted-foreground">{t('description')}</p>
        </div>
        <Button onClick={() => void refetch()} disabled={isLoading}>
          {isLoading ? t('loading') : t('refresh')}
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>{t('title')}</CardTitle>
          <CardDescription>{t('description')}</CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>{t('table.id')}</TableHead>
                <TableHead>{t('table.name')}</TableHead>
                <TableHead>{t('table.description')}</TableHead>
                <TableHead>{t('table.post-count')}</TableHead>
                <TableHead>{t('table.verified')}</TableHead>
                <TableHead>{t('table.created-at')}</TableHead>
                <TableHead className="text-right">
                  {t('table.actions')}
                </TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {tags.length === 0 ? (
                <TableRow>
                  <TableCell
                    colSpan={7}
                    className="text-center text-muted-foreground"
                  >
                    {isLoading ? t('fetching') : t('empty')}
                  </TableCell>
                </TableRow>
              ) : (
                tags.map((tag) => (
                  <TableRow key={tag.id}>
                    <TableCell className="font-mono text-sm">
                      {tag.id}
                    </TableCell>
                    <TableCell className="font-medium">{tag.name}</TableCell>
                    <TableCell className="max-w-xs truncate text-muted-foreground">
                      {tag.description || '-'}
                    </TableCell>
                    <TableCell>{tag.postCount}</TableCell>
                    <TableCell>
                      {tag.verified ? (
                        <Badge variant="default">{t('status.verified')}</Badge>
                      ) : (
                        <Badge variant="secondary">
                          {t('status.unverified')}
                        </Badge>
                      )}
                    </TableCell>
                    <TableCell className="text-sm text-muted-foreground">
                      {new Date(tag.createdAt).toLocaleDateString('ko-KR')}
                    </TableCell>
                    <TableCell className="text-right">
                      <div className="flex justify-end gap-2">
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => openEditDialog(tag)}
                        >
                          {t('actions.edit')}
                        </Button>
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => deleteTag(tag.id)}
                        >
                          {t('actions.delete')}
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <Dialog
        open={!!editingTag}
        onOpenChange={(open) => !open && setEditingTag(null)}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{t('edit-dialog.title')}</DialogTitle>
            <DialogDescription>
              {t('edit-dialog.description')}
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="tag-name">{t('edit-dialog.name-label')}</Label>
              <Input id="tag-name" value={editingTag?.name || ''} disabled />
            </div>

            <div className="space-y-2">
              <Label htmlFor="tag-description">
                {t('edit-dialog.description-label')}
              </Label>
              <Textarea
                id="tag-description"
                value={editDescription}
                onChange={(e) => setEditDescription(e.target.value)}
                placeholder={t('edit-dialog.description-placeholder')}
                rows={3}
              />
            </div>

            <div className="flex items-center space-x-2">
              <input
                type="checkbox"
                id="tag-verified"
                checked={editVerified}
                onChange={(e) => setEditVerified(e.target.checked)}
                className="h-4 w-4 rounded border-gray-300"
              />
              <Label htmlFor="tag-verified">
                {t('edit-dialog.verified-label')}
              </Label>
            </div>
          </div>

          <DialogFooter>
            <Button variant="outline" onClick={() => setEditingTag(null)}>
              {t('edit-dialog.cancel')}
            </Button>
            <Button onClick={handleSaveEdit}>{t('edit-dialog.save')}</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
