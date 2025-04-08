package logic.DTO;

public enum Role {
    ACADEMIC {
        @Override
        public void performAction1() {
            System.out.println("Acción 1 realizada por ACADEMICO");
        }

        @Override
        public void performAction2() {
            System.out.println("Acción 2 realizada por ACADEMICO");
        }
    },
    ACADEMIC_EVALUATOR {
        @Override
        public void performAction1() {
            System.out.println("Acción 1 realizada por ACADEMICO_EVALUADOR");
        }

        @Override
        public void performAction2() {
            System.out.println("Acción 2 realizada por ACADEMICO_EVALUADOR");
        }
    },
    COORDINATOR {
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