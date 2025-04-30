package logic.DTO;

public enum Role {
    ACADEMICO {
        @Override
        public void performAction1() {
            System.out.println("Acción 1 realizada por ACADEMICO");
        }

        @Override
        public void performAction2() {
            System.out.println("Acción 2 realizada por ACADEMICO");
        }
    },
    ACADEMICO_EVALUADOR {
        @Override
        public void performAction1() {
            System.out.println("Acción 1 realizada por ACADEMICO_EVALUADOR");
        }

        @Override
        public void performAction2() {
            System.out.println("Acción 2 realizada por ACADEMICO_EVALUADOR");
        }
    },

    GUEST {
        @Override
        public void performAction1() {
            System.out.println("Acción 1 realizada por ACADEMICO_EVALUADOR");
        }

        @Override
        public void performAction2() {
            System.out.println("Acción 2 realizada por ACADEMICO_EVALUADOR");
        }
    },

    COORDINADOR {
        @Override
        public void performAction1() {
            System.out.println("Acción 1 realizada por COORDINADOR");
        }

        @Override
        public void performAction2() {
            System.out.println("Acción 2 realizada por COORDINADOR");
        }
    };



    public abstract void performAction1();
    public abstract void performAction2();
}