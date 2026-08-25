import { computePosition, flip, shift } from '@floating-ui/react';
import {
  BrowsersIcon,
  CodeIcon,
  FunctionIcon,
  ImageIcon,
  ListBulletsIcon,
  ListChecksIcon,
  ListNumbersIcon,
  MathOperationsIcon,
  PaperclipIcon,
  QuotesIcon,
  TableIcon,
  TextBIcon,
  TextHTwoIcon,
  TextItalicIcon,
} from '@phosphor-icons/react';
import { TextHOneIcon } from '@phosphor-icons/react/dist/ssr';
import { Extension, ReactRenderer, posToDOMRect } from '@tiptap/react';
import type { Editor, Range } from '@tiptap/react';
import {
  Suggestion,
  type SuggestionOptions,
  type SuggestionProps,
} from '@tiptap/suggestion';
import { convertHangulToQwerty, getChoseong } from 'es-hangul';
import { useTranslations } from 'next-intl';
import {
  type RefAttributes,
  forwardRef,
  useEffect,
  useImperativeHandle,
  useState,
} from 'react';

import { Separator } from '@/components/ui/separator';
import { cn } from '@/lib/utils';

import { NodeType } from './nodes';
import type { MathTarget } from './nodes/math';

const MAX_COMMAND_QUERY_LENGTH = 12;

export type CommandName =
  | 'h1'
  | 'h2'
  | 'bold'
  | 'italic'
  | 'bullet'
  | 'numbered'
  | 'todo'
  | 'quote'
  | 'code'
  | 'table'
  | 'image'
  | 'file'
  | 'embed'
  | 'math'
  | 'inline-math';

export type CommandSearchTerms = Partial<Record<CommandName, string[]>>;

/**
 * 슬래시 메뉴만으로는 끝나지 않고 별도의 입력 창이 필요한 명령이 쓰는 통로다.
 * 확장은 에디터 바깥의 React 상태를 모르므로 여는 일은 호출부에 맡긴다.
 */
export interface CommandActions {
  openMathEditor?: (target: MathTarget) => void;
}

interface CommandsItemProps {
  name: CommandName;
  icon: React.ReactNode;
  command: (props: {
    editor: Editor;
    range: Range;
    actions: CommandActions;
  }) => void;
}

const commands: CommandsItemProps[] = [
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
      editor.chain().focus().deleteRange(range).setMark('bold').run();
    },
  },
  {
    name: 'italic',
    icon: <TextItalicIcon />,
    command: ({ editor, range }) => {
      editor.chain().focus().deleteRange(range).setMark('italic').run();
    },
  },
  {
    name: 'bullet',
    icon: <ListBulletsIcon />,
    command: ({ editor, range }) => {
      editor.chain().focus().deleteRange(range).toggleBulletList().run();
    },
  },
  {
    name: 'numbered',
    icon: <ListNumbersIcon />,
    command: ({ editor, range }) => {
      editor.chain().focus().deleteRange(range).toggleOrderedList().run();
    },
  },
  {
    name: 'todo',
    icon: <ListChecksIcon />,
    command: ({ editor, range }) => {
      editor.chain().focus().deleteRange(range).toggleTaskList().run();
    },
  },
  {
    name: 'quote',
    icon: <QuotesIcon />,
    command: ({ editor, range }) => {
      editor.chain().focus().deleteRange(range).toggleBlockquote().run();
    },
  },
  {
    name: 'code',
    icon: <CodeIcon />,
    command: ({ editor, range }) => {
      editor.chain().focus().deleteRange(range).setCodeBlock().run();
    },
  },
  {
    name: 'table',
    icon: <TableIcon />,
    command: ({ editor, range }) => {
      editor
        .chain()
        .focus()
        .deleteRange(range)
        .insertTable({ rows: 3, cols: 3, withHeaderRow: true })
        .run();
    },
  },
  {
    name: 'image',
    icon: <ImageIcon />,
    command: ({ editor, range }) => {
      editor
        .chain()
        .deleteRange(range)
        .insertContent([{ type: NodeType.Image }, { type: 'paragraph' }])
        .joinForward()
        .run();
    },
  },
  {
    name: 'file',
    icon: <PaperclipIcon />,
    command: ({ editor, range }) => {
      editor
        .chain()
        .deleteRange(range)
        .insertContent([{ type: NodeType.File }, { type: 'paragraph' }])
        .joinForward()
        .run();
    },
  },
  {
    name: 'embed',
    icon: <BrowsersIcon />,
    command: ({ editor, range }) => {
      editor
        .chain()
        .deleteRange(range)
        .insertContent([{ type: NodeType.Embed }, { type: 'paragraph' }])
        .joinForward()
        .run();
    },
  },
  {
    name: 'math',
    icon: <MathOperationsIcon />,
    command: ({ editor, range, actions }) => {
      openMathEditor(editor, range, actions, NodeType.BlockMath);
    },
  },
  {
    name: 'inline-math',
    icon: <FunctionIcon />,
    command: ({ editor, range, actions }) => {
      openMathEditor(editor, range, actions, NodeType.InlineMath);
    },
  },
];

/**
 * 수식은 빈 채로 넣어봐야 화면에 아무것도 남지 않으므로, 노드를 먼저 만들지 않고
 * 입력 창을 띄운 뒤 확정된 LaTeX 만 삽입한다.
 */
function openMathEditor(
  editor: Editor,
  range: Range,
  actions: CommandActions,
  type: MathTarget['type'],
) {
  editor.chain().focus().deleteRange(range).run();

  actions.openMathEditor?.({
    type,
    pos: editor.state.selection.from,
    latex: '',
    isNew: true,
  });
}

export const COMMAND_NAMES: CommandName[] = commands.map((item) => item.name);

const IME_COMPOSITION_KEY_CODE = 229;

function isImeComposing(event: KeyboardEvent) {
  return event.isComposing || event.keyCode === IME_COMPOSITION_KEY_CODE;
}

function normalize(value: string) {
  return value.toLowerCase().replace(/\s+/g, '');
}

function toSearchable(term: string) {
  const normalized = normalize(term);
  return [normalized, normalize(getChoseong(normalized))];
}

function filterCommands(query: string, searchTerms: CommandSearchTerms) {
  const normalizedQuery = normalize(query);
  const queries = [
    normalizedQuery,
    normalize(convertHangulToQwerty(normalizedQuery)),
  ];

  return commands.filter((item) =>
    [item.name, ...(searchTerms[item.name] ?? [])]
      .flatMap(toSearchable)
      .some((term) => queries.some((candidate) => term.includes(candidate))),
  );
}

interface CommandsExtensionOptions {
  suggestion: Partial<SuggestionOptions<CommandsItemProps>>;
  searchTerms: CommandSearchTerms;
  actions: CommandActions;
  /** 슬래시 메뉴에서 숨길 명령. 검색이 이름 매칭이라 옵션 없이는 특정 명령을 뺄 수 없다. */
  excludedCommands: CommandName[];
}

export const CommandsExtension = Extension.create<CommandsExtensionOptions>({
  name: 'commands',
  addOptions() {
    return {
      searchTerms: {},
      actions: {},
      excludedCommands: [],
      suggestion: {
        char: '/',
        startOfLine: false,
        allow: ({ editor }) => editor.isFocused,
        render: () => {
          let renderer:
            | ReactRenderer<
                CommandsRef,
                CommandsProps & RefAttributes<CommandsRef>
              >
            | undefined;

          const unmount = () => {
            if (!renderer) {
              return;
            }

            renderer.element.remove();
            renderer.destroy();
            renderer = undefined;
          };

          const mount = (props: CommandsProps) => {
            renderer = new ReactRenderer(Commands, {
              props,
              editor: props.editor,
            });

            (renderer.element as HTMLElement).style.position = 'absolute';
            document.body.appendChild(renderer.element);
            updatePosition(props.editor, renderer.element as HTMLElement);
          };

          const shouldShow = (props: CommandsProps) =>
            props.query.length < MAX_COMMAND_QUERY_LENGTH &&
            props.items.length > 0;

          const sync = (props: CommandsProps) => {
            if (!shouldShow(props)) {
              unmount();
              return;
            }

            if (!renderer) {
              mount(props);
              return;
            }

            renderer.updateProps(props);
            updatePosition(props.editor, renderer.element as HTMLElement);
          };

          return {
            onStart: sync,
            onUpdate: sync,
            onKeyDown(props) {
              if (!renderer) {
                return false;
              }

              if (props.event.key === 'Escape') {
                unmount();
                return true;
              }

              return renderer.ref?.onKeyDown(props) ?? false;
            },
            onExit: unmount,
          };
        },
      },
    };
  },
  addProseMirrorPlugins() {
    const { searchTerms, actions, excludedCommands } = this.options;

    return [
      Suggestion<CommandsItemProps>({
        editor: this.editor,
        ...this.options.suggestion,
        items: ({ query }) =>
          filterCommands(query, searchTerms).filter(
            (item) => !excludedCommands.includes(item.name),
          ),
        command: ({ editor, range, props }) =>
          props.command({ editor, range, actions }),
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

  const items = props.items;
  useEffect(() => {
    setSelectedIndex(0);
  }, [items]);

  const selectItem = (index: number) => {
    const item = props.items[index];
    if (item) {
      props.command(item);
    }
  };

  useImperativeHandle(ref, () => ({
    onKeyDown: ({ event }) => {
      if (isImeComposing(event)) {
        return false;
      }

      if (event.key === 'ArrowUp') {
        if (props.items.length !== 0) {
          setSelectedIndex(
            (prev) => (prev + props.items.length - 1) % props.items.length,
          );
        }

        return true;
      }

      if (event.key === 'ArrowDown') {
        if (props.items.length !== 0) {
          setSelectedIndex((prev) => (prev + 1) % props.items.length);
        }

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
          role="button"
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
        <span className="text-xs">{t('ignore.key')}</span>
      </div>
    </div>
  );
});
Commands.displayName = 'Commands';
