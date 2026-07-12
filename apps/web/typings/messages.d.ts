export {};

type Messages = typeof import('../public/locales/ko.json');

declare module 'next-intl' {
  interface AppConfig {
    Messages: Messages;
  }
}
