# Computes the spectrally shifted normalized laplacian
# Note: The normalized laplacian is required to have 0s on the diagonal!

function v = ssnl(A, x)

    n = size(A)(1);

    # Compute v0
    [u,v,w] = eigs(normalized_laplacian(A));
    v0 = u(:,n) / norm(u(:,n));
    if v0(1) < 0
        v0 = -v0;
    end

    # Our slightly modified normalized laplacian
    L = normalized_laplacian(A) - eye(n,n)

TODO: Continue here one the math docs are fleshed out!


endfunction