import { create } from 'zustand';
import { persist } from 'zustand/middleware';

const useExchangeStore = create(persist(
  (set, get) => ({
    exchanges: [],
    addExchange: (exchange) => set(state => ({
      exchanges: [...state.exchanges, exchange]
    })),
    clearExchanges: () => set({ exchanges: [] }),
  }),
  {
    name: 'exchange-logs',
    getStorage: () => localStorage,
  }
));

export default useExchangeStore;
