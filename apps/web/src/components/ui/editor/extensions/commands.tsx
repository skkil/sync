import { computePosition, flip, shift } from '@floating-ui/react';
import { TextBIcon, TextHTwoIcon, TextItalicIcon } from '@phosphor-icons/react';
import { TextHOneIcon } from '@phosphor-icons/react/dist/ssr';
import { Extension, ReactRenderer, posToDOMRect } from '@tiptap/react';
import type { Editor, Range } from '@tiptap/react';
import {
  Suggestion,
  type SuggestionOptions,
  type SuggestionProps,
} from '@tiptap/suggestion';
import { useTranslations } from 'next-intl';
import {
  type RefAttributes,
  forwardRef,
  useImperativeHandle,
  useState,
} from 'react';

import { Separator } from '@/components/ui/separator';
import { cn } from '@/lib/utils';

interface CommandsItemProps {
  name: string;
  icon: React.ReactNode;
  command: (props: { editor: Editor; range: Range }) => void;
}

interface CommandsExtensionOptions {
  suggestion: Partial<SuggestionOptions<CommandsItemProps>>;
}

export const CommandsExtension = Extension.create<CommandsExtensionOptions>({
  name: 'commands',
  addOptions() {
    return {
      suggestion: {
        char: '/',
        command({ editor, range, props }) {
          props.command({ editor, range });
        },
        items({ query }) {
          return (
            [
              {
                name: 'h1',
                icon: <TextHOneIcon />,
                command: ({ editor, range }) => {
                  editor
                    .chain()
                    .focus()
                    .deleteRange(range)
                    .setNode('heading', { level: 1 })
                    .run();
                },
              },
              {
                name: 'h2',
                icon: <TextHTwoIcon />,
                command: ({ editor, range }) => {
                  editor
                    .chain()
                    .focus()
                    .deleteRange(range)
                    .setNode('heading', { level: 2 })
                    .run();
                },
              },
              {
                name: 'bold',
                icon: <TextBIcon />,
                command: ({ editor, range }) => {
                  editor
                    .chain()
                    .focus()
                    .deleteRange(range)
                    .setMark('bold')
                    .run();
                },
              },
              {
                name: 'italic',
                icon: <TextItalicIcon />,
                command: ({ editor, range }) => {
                  editor
                    .chain()
                    .focus()
                    .deleteRange(range)
                    .setMark('italic')
                    .run();
                },
              },
            ] satisfies CommandsItemProps[]
          )
            .filter((item) => {
              return item.name.startsWith(query.toLowerCase());
            })
            .slice(0, 10);
        },
        startOfLine: false,
        allow: ({ editor }) => editor.isFocused,
        render: () => {
          let renderer: ReactRenderer<
            CommandsRef,
            CommandsProps & RefAttributes<CommandsRef>
          > | void;

          return {
            onStart(props) {
              if (!props.clientRect) {
                return;
              }

              renderer = new ReactRenderer(Commands, {
                props,
                editor: props.editor,
              });

              (renderer.element as HTMLElement).style.position = 'absolute';
              document.body.appendChild(renderer.element);
              updatePosition(props.editor, renderer.element as HTMLElement);
            },
            onUpdate(props) {
              if (!renderer) {
                return;
              }

              if (props.query.length >= 6 || props.items.length === 0) {
                renderer.element.remove();
                renderer.destroy();
                return;
              }

              renderer.updateProps(props);

              if (!props.clientRect) {
                return;
              }

              updatePosition(props.editor, renderer.element as HTMLElement);
            },
            onKeyDown(props) {
              if (props.event.key === 'Escape') {
                if (renderer) {
                  renderer.element.remove();
                  renderer.destroy();
                }

                return true;
              }

              return renderer?.ref?.onKeyDown(props) ?? false;
            },
            onExit: () => {
              if (renderer) {
                renderer.element.remove();
                renderer.destroy();
              }
            },
          };
        },
      },
    };
  },
  addProseMirrorPlugins() {
    return [
      Suggestion<CommandsItemProps>({
        editor: this.editor,
        ...this.options.suggestion,
      }),
    ];
  },
});

function updatePosition(editor: Editor, element: HTMLElement) {
  const velement = {
    getBoundingClientRect: () =>
      posToDOMRect(
        editor.view,
        editor.state.selection.from,
        editor.state.selection.to,
      ),
  };

  computePosition(velement, element, {
    placement: 'bottom-start',
    strategy: 'absolute',
    middleware: [shift(), flip()],
  }).then(({ x, y, strategy }) => {
    element.style.width = 'max-content';
    element.style.position = strategy;
    element.style.left = `${x}px`;
    element.style.top = `${y}px`;
  });
}

interface CommandsRef {
  onKeyDown: (props: { event: KeyboardEvent }) => boolean;
}

type CommandsProps = SuggestionProps<CommandsItemProps>;

const Commands = forwardRef<CommandsRef, CommandsProps>((props, ref) => {
  const t = useTranslations('components.editor.commands');

  const [selectedIndex, setSelectedIndex] = useState(0);

  const selectItem = (index: number) => {
    const item = props.items[index];
    if (item) {
      props.command(item);
    }
  };

  useImperativeHandle(ref, () => ({
    onKeyDown: ({ event }) => {
      if (event.key === 'ArrowUp') {
        setSelectedIndex(
          (selectedIndex + props.items.length - 1) % props.items.length,
        );
        return true;
      }

      if (event.key === 'ArrowDown') {
        setSelectedIndex((selectedIndex + 1) % props.items.length);
        return true;
      }

      if (event.key === 'Enter') {
        selectItem(selectedIndex);
        return true;
      }

      return false;
    },
  }));

  return (
    <div className="w-64 rounded-lg border bg-popover p-1 shadow-lg">
      {props.items.map((item, index) => (
        <div
          key={index}
          className={cn(
            'flex cursor-pointer items-center rounded-md',
            index === selectedIndex
              ? 'bg-primary text-primary-foreground'
              : 'hover:bg-accent',
          )}
          onClick={() => selectItem(index)}
        >
          <div className="flex items-center justify-center p-2">
            {item.icon}
          </div>

          <div className="flex flex-col">
            <span className="text-sm">{t(`${item.name}.title`)}</span>
            <span className="text-xs">{t(`${item.name}.description`)}</span>
          </div>
        </div>
      ))}

      <Separator />

      <div className="flex items-center justify-between px-3 py-2">
        <span className="text-sm">{t('ignore.title')}</span>
        <span className="text-xs">ESC</span>
      </div>
    </div>
  );
});
Commands.displayName = 'Commands';
